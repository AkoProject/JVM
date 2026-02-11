
val rainVersion = "1.0.0-DEV7"
val smartWebVersion = "1.0.0-DEV9"
val smartAccessVersion = "1.0.0-DEV6"
dependencies{
    api(project(":ako-core"))
    api(project(":ako-jpa"))

    implementation("com.IceCreamQAQ.Rain:application:${rainVersion}")
    implementation("com.IceCreamQAQ.Rain:controller:${rainVersion}")
    implementation("com.IceCreamQAQ.SmartWeb.Server:SmartHTTP:$smartWebVersion")
    implementation("com.IceCreamQAQ.SmartAccess:hibernate5:$smartAccessVersion")


    testImplementation("com.h2database:h2:2.4.240")
}