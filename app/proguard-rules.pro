# YBOW Browser keep rules.
# The app deliberately exposes no JavaScript bridge, so no @JavascriptInterface
# classes need to be kept. Room and Jetpack Compose ship their own consumer rules.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
