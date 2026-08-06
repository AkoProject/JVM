
val rainVersion = "1.0.0-DEV7"
val smartWebVersion = "1.0.0-DEV9"
val smartAccessVersion = "1.0.0-DEV11"
dependencies{
    api(project(":ako-core"))
    api(project(":ako-jpa"))

    compileOnly("com.IceCreamQAQ.Rain:application:$rainVersion")
    compileOnly("com.IceCreamQAQ.Rain:controller:$rainVersion")
    compileOnly("com.IceCreamQAQ.SmartWeb.Server:SmartHTTP:$smartWebVersion")
    compileOnly("com.IceCreamQAQ.SmartAccess:hibernate5:$smartAccessVersion")

    testImplementation("com.IceCreamQAQ.Rain:application:$rainVersion")
    testImplementation("com.IceCreamQAQ.Rain:controller:$rainVersion")
    testImplementation("com.IceCreamQAQ.SmartWeb.Server:SmartHTTP:$smartWebVersion")
    testImplementation("com.IceCreamQAQ.SmartAccess:hibernate5:$smartAccessVersion")


    testImplementation("com.h2database:h2:2.4.240")
}

kotlin {
    compilerOptions{
        freeCompilerArgs.add("-jvm-default=enable")
    }
}