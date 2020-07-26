# Greyscale

# User Guide
In order to be able to use this app properly, you should grant special permission via **ADB** or **ROOT**.

## Why?
Instead of the app constantly running in the background and drain the battery, we prefer to use a simple option that is already in all Android devices, hidden in ```Settings > Developer Option > Simulate color space```, however, this is really uncomfortable if you want to switch between normal and greyscale many times.\
Since this function is part of the Android OS and not a normal option available to developers, it requires special permission.


## How?
#### Root
If your phone is rooted it's actually pretty simple, you just have to press the **root** button that will appear in the popup when you try to enable the greyscale.
#### ADB
ADB (Android Debug Bridge) is a tool that everyone can download, and it allows you to send special commands from your computer to your phone (usually via USB cable).

Here's the official page of [Android Debug Bridge](https://developer.android.com/studio/command-line/adb).

And here's a complete guide to installing it on MacOS, Windows or Linux: [XDA - How to install adb](https://www.xda-developers.com/install-adb-windows-macos-linux/).

Once you have adb correctly installed in your computer you're almost done, what you have to do now is connect your phone to your computer, open the Terminal and simply paste this text and hit enter:

    adb shell pm grant io.github.rsookram.greyscale android.permission.WRITE_SECURE_SETTINGS

You're done, enjoy Greyscale+!
