## 📷 Image Picker Library

Easy configurable library to pick and take picture from gallery and camera

[![](https://jitpack.io/v/SyedAmmarSohail/ImagePicker.svg)](https://jitpack.io/#SyedAmmarSohail/ImagePicker)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)
[![API](https://img.shields.io/badge/API-18%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=18)

Use CameraX for ➡️ [![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)

Use Camera1 for ➡️ [![API](https://img.shields.io/badge/API-18%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=18)

## 🎞️ Demo

![](https://github.com/SyedAmmarSohail/ImagePicker/blob/master/art/image_picker.gif)

## 💻 Installation

Add the following maven repositories in root build.gradle:
```
allprojects {
    repositories {
        ...
        maven { url "https://maven.google.com" }
        maven { url "https://jitpack.io" }
    }
}
```

Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.SyedAmmarSohail:ImagePicker:$libVersion'
}
```
where libVersion -> [![Releases](https://img.shields.io/github/release/SyedAmmarSohail/imagePicker/all.svg?style=flat-square)](https://github.com/SyedAmmarSohail/ImagePicker/releases)

Add in Manifest
```
    <uses-sdk
        android:minSdkVersion="YOUR_MIN_SDK"
        tools:overrideLibrary="androidx.camera.view, androidx.camera.camera2, androidx.camera.lifecycle, androidx.camera.core" />
```

Start the Image Picker
```
            ImagePicker.with(this)
                .cameraOnly()
                .cropOval()
                .start { resultCode, data ->
                    if (resultCode == RESULT_OK && data != null) {
                        val picker = ImagePicker.getPickerData(data)

                        findViewById<ImageView>(R.id.iv).setImageURI(picker?.uri)

                    }
                }
```

To change the switch camera and gallery icon
```
            ImagePicker.with(this)
                .cameraOnly()
                .cropOval()
                .setGalleryIcon(R.drawable.gallery)
                .setCameraSwitchIcon(R.drawable.switch_camera)
                .start { resultCode, data ->
                    if (resultCode == RESULT_OK && data != null) {
                        val picker = ImagePicker.getPickerData(data)

                        findViewById<ImageView>(R.id.iv).setImageURI(picker?.uri)

                    }
                }
```

For compress the image result
```
                .compressImage(1024, 1024)
```

For specific image size result
```
                .maxResultSize(200, 200)

```

## 📃 Libraries Used
* uCrop [https://github.com/Yalantis/uCrop](https://github.com/Yalantis/uCrop)
