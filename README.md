# JVDF

A parser for Valve Software's KeyValues ("VDF") format, commonly used by Source engine games, written entirely in Java.

## Features

* Zero dependencies.
* Very fast. The 3MB, 105k line CS:GO item schema was parsed on my machine in about 30ms when HotSpot was warmed up.
* Multi-threaded preprocessor and binder.
* Memory efficient.  The resultant tree structures are often a tenth (1/10) the size of the input text.
* Java object binding (see below).
* Standalone preprocessor can convert human-readable VDF documents into much smaller, valid VDF documents with whitespace and comments stripped.
* Fully compliant with the VDF format, as documented on the [Valve Developer Wiki](https://developer.valvesoftware.com/wiki/KeyValues).
* Exposed API is thoroughly unit tested.  The parser was also validated against CS:GO's item schema.

## Example

*VDF document*
```
"key1"      "value1"
"key2"      "value2"
"root_node"
{
    "key3"      "value3"
    ...
    "child_node"
    {
        "child1"        "child value 1"
        "child2"        "child value 2"
    }
}
```

### Using the VDFParser
```
VDFNode node = new VDFParser().parse(...);

node.getString("key1")              => value1
node.getString("key2")              => value2

node.getSubNode("root_node")
    .getString("key3")              => value3

node.getSubNode("root_node")
    .getSubNode("child_node")
    .getString("child2")            => child value 2
```

### Using the VDFBinder

```

class DocumentNode {
    @VDFBindField(keyName = "root_node")
    RootNode root;
    
    @VDFBindField
    String key1;
    
    @VDFBindField
    String key2;
}

class RootNode {
    @VDFBindField
    String key3;
    
    @VDFBindField(keyName = "child_node")
    ChildNode childNode;
}

class ChildNode {
    @VDFBindField
    String child1;
    
    @VDFBindField(keyName = "child2")
    String unrelated;   // note the annotation above binds this to "child2"
}

DocumentNode doc = new DocumentNode();
new VDFBinder(new VDFParser().parse(...)).bindTo(doc);

doc.key2                            => value2
doc.root.key3                       => value3
doc.root.childNode.child1           => child value 1
doc.root.childNode.unrelated        => child value 2
```

> **NOTE:** While the binder is capable of resolving member classes, only member classes that are defined in the same class that they are referenced can be initialized.

## License

Copyright 2017 Platinum Digital Group LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.