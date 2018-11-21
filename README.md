# Mirror (Kotlin Artificial Reflection)
By Lightning Kite

Status: In development

A plugin/runtime combination built for serializing and reflecting on objects in pure Kotlin common, making your code solid across platforms.

The plugin generates Kotlin files describing the classes you specify.

The runtime can take that reflected data, and using it, can serialize things to JSON for you.

Other formats, such as SQL, are coming soon.


# Plugin

## Set up (for now)

Download this to your machine and run the Gradle task "publishToMavenLocal" on the "mirror-plugin" project.  You'll then have access to it through build scripts as follows:

```groovy

buildscript {
    ...
    repositories {
        mavenLocal()
        ...
    }
    ...
    dependencies {
        ...
        classpath "com.lightningkite:mirror-plugin:0.0.1"
    }
}
apply plugin: 'com.lightningkite.mirror'

```

Adding the plugin will add a `mirror` task to your project.

The `mirror` task will look for files ending with `mirror.txt`, and will add generated files next to them.

The generated files will have information about the classes you specify in the form of a `ClassInfoRegistry`.

After registering the data, you can use the object `SomeType::class.info` to access the metadata.  You can also access it more directly using `SomeTypeClassInfo`.

## Mirror File

The format of `mirror.txt` is as follows:

- Blank lines are ignored
- The first two lines define where you want your module object:
    - The first line is the package name
    - The second line is the name of the module object.
- All remaining lines are the fully-qualified names of classes you want reflective information about.

Example:

```text

package.for.module.object

ModuleObject

fully.qualified.name.to.the.Class
com.lightningkite.recktangle.Point
keep.putting.qualified.Names

```


## Development details

How does it work?  It reads `kotlin_metadata` files from your libraries using [kotlin-metadata](https://github.com/Takhion/kotlin-metadata) and reads your source code using an ANTLR4 parser.

It takes the data read and outputs some basic code files that are implementations of interfaces found in the runtime.



# Runtime

The runtime contains the interfaces that will be implemented by the plugin, as well as a serialization/deserialization system.

```kotlin
JsonSerialization.write(4, Int::class.type) // Yields "4"
JsonSerialization.write(listOf("string1", "string2"), String::class.type.list) // Yields the next line:
//["string1","string2"]
```

Make sure you use your module to serialize or deserialize things through reflection.  See [Mirror File](#mirror-file).

```kotlin
setupFunctionName()
```


## What is and isn't serialized

When using the automatic reflective serializers, only fields that are introduced using `val` or `var` in the primary constructor are used.  If you need to add other fields, write your own serializer.


## Custom serialization

### Within existing formats

```kotlin
data class MySpecialType(val x: Int = 3)

fun setup(){
    val intEncoder = JsonSerializer.encoder(Int::class.type)
    val intDecoder = JsonSerializer.decoder(Int::class.type)
    JsonSerializer.addEncoder(MySpecialType::class.type) { it:MySpecialType ->
        intEncoder.invoke(this, it.x)
    }
    JsonSerializer.addDecoder(MySpecialType::class.type) {
        MySpecialType(x = intDecoder.invoke(this))
    }
}

```

### Manual processing

Be warned!  This method allows you to encode/decode things outside of the JSON standard!  Be careful!

```kotlin
data class MySpecialType(val x: Int = 3)

fun setup(){
    JsonSerializer.addEncoder(MySpecialType::class.type) { it:MySpecialType ->
        //Receiver is Appendable, because this is JSON
        //You are responsible for making valid JSON this way.
        append(it.x.toString())
    }
    JsonSerializer.addDecoder(MySpecialType::class.type) {
        //Receiver is a special character iterator called CharIteratorReader.
        MySpecialType(x = readWhile{ it.isDigit() }.toInt())
    }
}

```


## Adding Formats

Adding your own formats is pretty easy.  Just extend `Encoder` and `Decoder`.  Look at [`JsonSerializer`](mirror-runtime/src/commonMain/kotlin/com/lightningkite/mirror/serialization/json/JsonSerializer.kt) as an example. 


# Known Limitations

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