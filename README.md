# Mirror (Kotlin Artificial Reflection)
By Lightning Kite

Status: In development

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

The `mirror` task will look for files ending with `mirror.txt`, and will add next to them generate files for reflection.

## `mirror.txt`

The format of `mirror.txt` is as follows:

- Blank lines are ignored
- The first line with text is the package for the setup function
- The second line is the name of the setup function
- All remaining lines are fully-qualified names to 

Example:

```text

package.for.setup.function

setupFunctionName

fully.qualified.name.to.the.Class
com.lightningkite.recktangle.Point
keep.putting.qualified.Names

```

## Output

The result will be objects in the same package as the classes you want reflected with `ClassInfo` appended to the end.

For example, if I asked for `com.lightningkite.recktangle.Point`, I'll get an object with the fully qualified name `com.lightningkite.recktangle.PointClassInfo`, which will have metadata about the class, a method for constructing an instance, and field objects for retrieving the value of a field.

# Runtime

The runtime contains the interfaces that will be implemented by the generated reflected data, as well as a serialization/deserialization system.

```kotlin
JsonSerialization.write(4, Int::class.type) // Yields "4"
JsonSerialization.write(listOf("string1", "string2"), String::class.type.list) // Yields the next line:
//["string1","string2"]
```

## Adding Formats

Adding your own formats is pretty easy.  Just extend `Encoder` or `Decoder`.  Look at `JsonSerializer` as an example. 

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