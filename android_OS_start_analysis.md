#Android 启动源码分析

#启动过程

1. 电源上位->bootloader 初始化一些硬件资源->加载linux 内核->Android 第一个用户空间的进程。


##init.exe

Android.mk->是用来配置相关的源文件，用来给编译器进行编译相关的源文件，并会编译成共享库(.so)，或者 可执行文件，还有静态链接库。

**主要看init.cpp**

主要的代码分析

	Parser& parser = Parser::GetInstance();
    parser.AddSectionParser("service",std::make_unique<ServiceParser>());
    parser.AddSectionParser("on", std::make_unique<ActionParser>());
    parser.AddSectionParser("import", std::make_unique<ImportParser>());
    // 1.解析初始化配置文件
    parser.ParseConfig("/init.rc");

    ......

    // 2.执行命令，启动服务
    while (true) {
        if (!waiting_for_exec) {
            am.ExecuteOneCommand();
            restart_processes();
        }

**init.rc(system-core/rootdir/init.rc)**

这个里头定义了三个section

1. Action
2. Service
3. Import

**parser.ParseConfig("/init.rc")基本过程

* ParseConfig->ParseConfigFile->ParseData(真正解析init.rc的配置)->找到相应的parser根据关键字on,service,import->相应的parser的parseSection

	section_parser = section_parsers_[args[0]].get();

    std::string ret_err;
    if (!section_parser->ParseSection(args, &ret_err)) {
        parse_error(&state, "%s\n", ret_err.c_str());
        section_parser = nullptr;
    }

* import /init.${ro.zygote}.rc

   **service zygote /system/bin/app_process64 -Xzygote /system/bin --zygote --start-system-server**


    class main
    socket zygote stream 660 root system
    onrestart write /sys/android_power/request_state wake
    onrestart write /sys/power/state on
    onrestart restart media
    onrestart restart netd
    writepid /dev/cpuset/foreground/tasks /sys/fs/cgroup/stune/foreground/tasks

zygote -> 进程的名字
/system/bin/app_process64 -> 可执行文件的路径和名字

-Xzygote /system/bin --zygote --start-system-server ->参数。

* 如何启动服务

    // 3. 启动受精卵进程
    restart_processes->ServiceManager::GetInstance().ForEachServiceWithFlags()->Service::RestartIfNeeded->Service::Start()->fork()->execve(可执行文件的名字，变量，环境)->启动zygote受精卵

// 在当前进程，克隆出一个进程
// 如果pid>0,就是父进程，如果=0就是子进程，否则<0,因为某种原因，fork失败
pid_t pid = fork();

* app_main(framework_base/cmds/app_process, zygote进程)

	// 4. 启动运行时
	runtime.start("com.android.internal.os.ZygoteInit", args, zygote);
	
	// 5. 从C++/C的世界进入到java世界了
	jmethodID startMeth = env->GetStaticMethodID(startClass, "main",
	"([Ljava/lang/String;)V");
	
	env->CallStaticVoidMethod(startClass, startMeth, strArray);

* ZygoteInit.java(platform_frameworks_base-master\core\java\com\android\internal\os)

    //6. 注册并且打开本地socket，用来接受ActivityManagerService发过来的请求，要求创建一个新的子进程，用来运行Android APP
    registerZygoteSocket(socketName);
    
    //7. 启动system server

    if (startSystemServer) {

        startSystemServer(abiList, socketName);
    }

    //8.处理启动system server
   
    handleSystemServerProcess(parsedArgs);

    //9.启动system server
    RuntimeInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs, cl);
    
    //10.开始抛异常ZygoteInit.MethodAndArgsCaller
    applicationInit(targetSdkVersion, argv, classLoader);

    //11. Remaining arguments are passed to the start class's static main
    invokeStaticMain(args.startClass, args.startArgs, classLoader);
    
    //12.抛异常，并且把类名SystemServer,和参数传进去，参数实际上没有内容
    throw new ZygoteInit.MethodAndArgsCaller(m, argv);

    //13.执行 SystemServer.main()
    MethodAndArgsCaller.run()
    
* SystemServer.java(platform_frameworks_base-master\services\java\com\android\server)

这正的启动Android Framework

    //14.准备mainLooper
    Looper.prepareMainLooper();

    //15.加载anroid_servers.so, platform_frameworks_base-master\services\anroid.mk
    // Initialize native services.
    System.loadLibrary("android_servers");

    //16.启动框架服务
    startBootstrapServices();
    startCoreServices();
    startOtherServices();
    
    //17.调用ActivityManagerService SystemReady
    // We now tell the activity manager it is okay to run third party
    // code.  It will call back into us once it has gotten to the state
    // where third party code can really run (but before it has actually
    // started launching the initial applications), for us to complete our
    // initialization.
    mActivityManagerService.systemReady
    
    //18.启动home launcher
    startHomeActivityLocked(mCurrentUserId, "systemReady");

    

    



    