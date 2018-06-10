
# ReadMoreTextView
The ExpandableTextView library lets you create a TextView that can be expanded upon clicking the "... More" text.

![Example](https://media.giphy.com/media/mJR29bRsPnGJhK0rJK/giphy.gif)

## Usage
Add the ExpandableTextView view to your XML and add the necessary attributes, as seen in the following example:

```
<com.vanethos.expandabletextview.ExpandableTextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textSize="16sp"
    android:text="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi mattis condimentum odio id facilisis.\nMauris semper leo a aliquet laoreet. Nunc tincidunt ultrices orci, sollicitudin rhoncus lacus tincidunt non. Vivamus molestie finibus tortor, ac blandit tellus gravida finibus. Pellentesque cursus lorem a consequat condimentum. Sed vel ligula feugiat, posuere lorem vitae, dignissim massa. Vestibulum non diam quis diam laoreet rhoncus. Sed vel ante ac dui rutrum convallis. Sed tempor ante vel consectetur consectetur. Pellentesque ut sem ac mauris sagittis ullamcorper nec non sem. Morbi tincidunt consectetur varius. In hac habitasse platea dictumst. Ut orci odio, iaculis at porta in, tempus nec libero.  Ut orci odio, iaculis at porta in, tempus. "
    app:needsReadLess="true"
    app:underlineEllipsize="false"
    app:readLessLabel="...less!"
    app:readMoreLabel="...more?"
    app:ellipsizeColor="#470"
    android:maxLines="3"
    />

```

### Attributes

`app:needsReadLess="true"`
- Text can be collapsed by clicking on a *Read Less* handle

`app:underlineEllipsize="false"`
- Underlines the *Read More* and *Read Less* text

`app:readLessLabel="...less!"`
- Changes the *Read Less* text handle to the specified text

`app:readMoreLabel="...more?"`
- Changes the *Read More* text handle to the specified text

`app:ellipsizeColor="#470"`
- Changes the the *Read More* and *Read Less* text color

`android:maxLines="3"`
- Sets the maximum number of lines for the collapsed text

## Installation

On your `build.gradle` root file, add:

```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

On your `build.gradle` app file add the dependency:

```
implementation 'com.github.Vanethos:ExpandableTextView:v0.1.0'
```



