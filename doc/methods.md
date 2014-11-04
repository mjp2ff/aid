# Benchmark Methods

## Positive Test Cases

### java.io.File: `canRead()`
#### Java standard library

```
public boolean canRead();
```

* Follows pattern of summary sentence
* Summary verb requires analysis of structure and return type
* Requires WordNet to find similarity between "can read" and ```fs.checkAccess()```
* TF/IDF reduces importance of the phrase "abstract pathname" since it appears in many methods

### java.util.ArrayList: `indexOf()`
#### Java standard library

```
public int indexOf(Object o);
```

* Follows pattern of summary sentence
* WordNet/NLP can reconcile "Returns the index of" with the method name "indexOf"
* Relies on control flow (including return statements) with alternative return value
* TF/IDF and/or WordNet identify "element" as a valid representation of the field "elementData"

### edu.rice.cs.drjava.model.AbstractDJDocument: `setTab()`
#### drjava

```
public void setTab(String tab, int pos);
```

* Main action verb based on name of a main function call
* Talks about use of parameters to perform update

### java.lang.String: `compareTo()`

```
public int compareTo(String anotherString);
```

* Follows pattern of summary sentence.
* Stemming can reconcile 'compareTo' and 'compares'
* Significant/lengthy comment can stress TF/IDF.

### java.util.HashMap: `put()`
#### Java standard library

```
public V put(K key, V value)
```

* Follows pattern of summary sentence.
* Specifically documents some outside method calls appropriately.
* Relies on WordNet/NLP for equivalence of 'addEntry'/'puts', 'retrieved'/'access', etc.

## Negative Test Cases

### java.net.Socket: `connect()`
#### Java standard library

```
public void connect(SocketAddress endpoint, int timeout) throws IOException;
```

* Doesn't define conditions for success
* Doesn't talk about state update for `connected` and `bound` fields

### edu.rice.cs.drjava.ui.ExternalProcessPanel: `doubleClicked()`
#### drjava

```
public void doubleClicked(MouseEvent e);
```

* Makes no mention of what is done in the method, just why it is called
* Makes no mention of relevant information or updated state

### org.jedit.migration.OneTimeMigrationService: `execute()`
#### jedit 4.2

```
public static void execute();
```

* Briefly and insufficiently explains what the method does, specifies method call incorrectly.
* Does not discuss data or method flow, purpose, or details of actions performed.
* Relies heavily on general class information to explain method purpose.

### devplugin.Date.java: `readData()`
#### tvbrowser 2.5.3

```
public static Date readData(final DataInput in) throws IOException;
```


* Describes method behavior only in a vague, general way that restates the method name.
* Spelling errors in Javadoc header.
* No mention of completely different behavior based on version of DataInput.
* No explanation of how each version is handled individually.

### org.hsqldb.cmdline.SqlTool.java: `objectMain()`

```
public static void objectMain(String[] arg) throws SqlToolException;
```

* Only has high-level overview of method purpose and behavior, with a big picture description in-lined.
* Lack of documentation about specific method decisions in a massive method, indicative that the comments should be greatly expanded or the method should be sub-divided into smaller method.
* Relies on outside documentation (that isn't present or referenced in the comments) to explain method actions.