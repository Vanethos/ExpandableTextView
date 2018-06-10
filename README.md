
# ReadMoreTextView
The ExpandableTextView library lets you create a TextView that can be expanded upon clicking the "... More" text.

![Example](https://media.giphy.com/media/mJR29bRsPnGJhK0rJK/giphy.gif)

## Usage
Add the ExpandableTextView view to your XML and add the necessary attributes, as seen in the following example:

```
<com.vanethos.expandabletextview.ExpandableTextView
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"
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


