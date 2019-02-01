# Mirror (Kotlin Artificial Reflection)
By Lightning Kite

 Plugin: [ ![Download](https://api.bintray.com/packages/lightningkite/com.lightningkite.krosslin/mirror-plugin/images/download.svg) ](https://bintray.com/lightningkite/com.lightningkite.krosslin/mirror-plugin/_latestVersion) 
 
 Runtime: [ ![Download](https://api.bintray.com/packages/lightningkite/com.lightningkite.krosslin/mirror-runtime/images/download.svg) ](https://bintray.com/lightningkite/com.lightningkite.krosslin/mirror-runtime/_latestVersion)

Status: Working, first public release

An extension to KotlinX Serialization, built for serializing and getting reflective information about classes at runtime in pure Kotlin common.

The plugin generates Kotlin files describing the classes you specify in a file called `mirror.txt`, as described below.

You can then access the serializer and reflective data for a class by writing/importing `MyClassMirror`.

## Advantages over KotlinX serialization alone

- Generated information provides a direct list of fields a class contains, as well as accessors to the fields.
- This plugin allows you to serialize references to classes and fields.
- This plugin works by using a manifest file instead of annotations, and thus does not fill up your code with annotations.
- This plugin supports polymorphism on all platforms, and `@Polymorphic` is not necessary.
- This plugin can read classes from dependencies declared in your Gradle build file, and thus can serialize/deserialize/give reflective information about any class in your dependencies.
- This plugin does *not* use any magic.  It simply does the repetitive task of writing serializers and reflective information into code files for you.  As such, you'll be able to read its output yourself.
- Performance is equivalent, see section below.

## Limitations

- The runtime doesn't work on Kotlin Native yet due to [KT-29635](https://youtrack.jetbrains.com/issue/KT-29635), a bug in the compiler.  May be my own fault, but I can't fix it if I don't know what's wrong.
- You cannot customize the serialized names of fields yet.
- Annotations don't work the same, because you can't instantiate instances of annotations without cheating by bypassing the compiler.  Instead, when you get the annotations, they will return instances of `AnnotationNameMirror`, which have all of the same fields.
- You must call some code to set up the polymorphic information, as explained below.
- If you try doing weird things to the classes you serialize, like giving them names with tic marks, the plugin might not work correctly.
- The ANTLR4 specification for Kotlin I'm using doesn't seem to be dead accurate.  If you put strange things in the file, you'll get strange results.  Try to keep the files that you use reflection on simple.
- When using reflection on classes from outside your project, optional fields not present on deserialization are expensive.  This is because when you are reflecting on classes from other libraries, the plugin is unable to obtain the instructions used to set defaults for fields.  As such, it uses a workaround that invokes the constructor once for every missing optional field.  
- The plugin cannot yet handle short references to classes within classes.  For example:

```kotlin
class Owner {
    class Child(val sibling: Child?)
}
```

doesn't work.  Use this instead:

```kotlin
class Owner {
    class Child(val sibling: Owner.Child?)
}
```


## Setup

```groovy

buildscript {
    ext.mirrorVersion = '0.1.1'
    ...
    repositories {
        maven { url 'https://dl.bintray.com/lightningkite/com.lightningkite.krosslin' }
        ...
    }
    ...
    dependencies {
        ...
        classpath "com.lightningkite:mirror-plugin:${mirrorVersion}"
    }
}
apply plugin: 'com.lightningkite.mirror'

repositories {
    maven { url 'https://dl.bintray.com/lightningkite/com.lightningkite.krosslin' }
    ...
}
...
dependencies {
    ...
    //Depending on the version you need
    api "com.lightningkite:mirror-runtime-metadata:${mirrorVersion}"
    api "com.lightningkite:mirror-runtime-jvm:${mirrorVersion}"
    api "com.lightningkite:mirror-runtime-js:${mirrorVersion}"
    api "com.lightningkite:mirror-runtime-ios:${mirrorVersion}"
    api "com.lightningkite:mirror-runtime-iosx64:${mirrorVersion}"
}
```

## Usage

Adding the plugin will add a `mirror` task to your project.

The `mirror` task will look for files ending with `mirror.txt`, which have information in them about what classes it should write serializers for.  These files are formatted as follows:

```text
//What package/function name to output the registration function to
//This registration function should be called at startup to register classes for polymorphism

name = my.packagename.registerMyTypes

//The relative path to output the metadata code files to
//This is optional.  If not present, it uses the same directory.

output = ../kotlin/mirror

//Then just write the qualified names of each class you want to serialize or have reflective information about.
//Note that primitives and basic collections are already included.

kotlin.Pair
my.packagename.MyCustomClass
my.packagename.MyCustomEnum
my.packagename.MyCustomInterface

//Anything the given classes extends/implements or uses in a field is implicitly analyzed as well.

```

You'll now be able to use the reflective information after running the Gradle `mirror` task like this:

```kotlin
package my.packagename.test

import my.packagename.registerMyTypes
import my.packagename.MyCustomClass
import my.packagename.MyCustomClassMirror
import kotlinx.serialization.json.Json

fun main() {
    registerMyTypes() //IMPORTANT!  You must call this before doing any serialization.

    val instanceToSerialize = MyCustomClass(x = 3)
    val str = Json.plain.stringify(MyCustomClassMirror, instanceToSerialize)
    println(str) //Returns JSON for instanceToSerialize
    
    val instanceRead = Json.plain.parse(MyCustomClassMirror, str)
    assert(instanceRead == instanceToSerialize)
}
```

## How the plugin works

How does it work?  It reads `kotlin_metadata` files from your libraries using [kotlin-metadata](https://github.com/Takhion/kotlin-metadata) and reads your source code using an ANTLR4 parser.

It takes the data read and writes normal Kotlin classes for you that give information about the requested types. 

## What is and isn't serialized

When using the automatic reflective serializers, only fields that are introduced using `val` or `var` in the primary constructor are used.  If you need to add other fields, write your own serializer.


## Custom serialization

Write an implementation of `MirrorClass<T>` for your special object and place it anywhere you'd like inside your codebase *except* the directory where the normal Mirror files are output.


## Contributing

I'm happy for contributions!  It should be ready-to-build on your own machine, and I'll happily review PRs.


## Performance VS Plain Serialization

```
Serialize KotlinX: 0.00948 ms
Serialize Mirror: 0.00887 ms
Serialize Mirror/Kotlinx: 0.9356540084388184
Round Trip KotlinX: 0.02416 ms
Round Trip Mirror: 0.023775 ms
Round Trip Mirror/Kotlinx: 0.9840645695364238
Deserialize KotlinX: 0.01328 ms
Deserialize Mirror: 0.012285 ms
Deserialize Mirror/Kotlinx: 0.9250753012048193```