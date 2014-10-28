# Benchmark Methods

## Positive Test Cases

### java.io.File: `canRead()`

```
public boolean canRead();
```

* Follows pattern of summary sentence
* Summary verb requires analysis of structure and return type
* Requires WordNet to find similarity between "can read" and ```fs.checkAccess()```
* TF/IDF reduces importance of the phrase "abstract pathname" since it appears in many methods

### java.util.ArrayList: `indexOf()`

```
public int indexOf();
```

* Follows pattern of summary sentence
* WordNet/NLP can reconcile "Returns the index of" with the method name "indexOf"
* Relies on control flow (including return statements) with alternative return value
* TF/IDF and/or WordNet identify "element" as a valid representation of the field "elementData"

### edu.rice.cs.drjava.model.AbstractDJDocument: `setTab()`

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

```
public V put(K key, V value)
```

* Follows pattern of summary sentence.
* Specifically documents some outside method calls appropriately.
* Relies on WordNet/NLP for equivalence of 'addEntry'/'puts', 'retrieved'/'access', etc.

## Negative Test Cases

### java.net.Socket: `connect()`

```
public void connect(SocketAddress endpoint, int timeout) throws IOException;
```

* Doesn't define conditions for success
* Doesn't talk about state update for `connected` and `bound` fields

### edu.rice.cs.drjava.ui.ExternalProcessPanel: `doubleClicked()`

```
public void doubleClicked(MouseEvent e);
```

* Makes no mention of what is done in the method, just why it is called
* Makes no mention of relevant information or updated state

