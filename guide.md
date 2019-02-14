# User Guide
In order to be able to use this app properly you should grant a special permission via **ADB** or **ROOT**.

## Why?
Because to avoid battery drain making this app constantly running in background, we preferred to use a simple option that is already in all Android devices, hidden in 
```Settings > Developer Option > Simulate color space```, however, this is really uncomfortable to reach every time you want to switch between colors and greyscale.
Since this function is part of the Android OS and not a normal option available to developers, it requires a special permission.


## How?
#### Root
If you phone is rooted it's actually pretty simple, you just have to press the **root** button that will appear in the popup when you try to enable the greyscale.
#### ADB
ADB (Android Debug Bridge) is a tool that everyone can download, and it allows you to send special commands from you computer to your phone (usually via USB cable).

Here's the official page [Android Debug Bridge](https://developer.android.com/studio/command-line/adb)
And Here's a complete guide to install it on MacOS, Windows or Linux [XDA - How to install adb](https://www.xda-developers.com/install-adb-windows-macos-linux/)

Once you have adb correctly installed in your computer you're almost done, what you have to do now is connect your phone to your computer, open the Terminal and simply paste this text and hit enter

    adb -d shell pm grant com.berenluth.grayscale android.permission.WRITE_SECURE_SETTINGS

You're done!
