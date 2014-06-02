GooglyZoo
=========

![Googly Zoo banner](http://tvbarthel.github.io/images/googly.png)

Googly Zoo is an augmented virtuality experience. Choose your favorite virtual Googly pet and simply move your face in front of your smartphone to wake it up. Once your virtual pet is awake, its Googly Eyes will track you for hours without blinking! Even if your new virtual pet doesn't show any signs of tiredness, it will automatically fall asleep as soon as you disappear from your screen.

[Short video on YouTube](https://www.youtube.com/watch?v=hu4kZRP5mZA)

[Available on the Google Play](https://play.google.com/store/apps/details?id=fr.tvbarthel.attempt.googlyzooapp)

SmoothFaceDetectionListener
=========

Googly Zoo is an head tracking attempt developed to experiment FaceDetection listener. Based on Camera.FaceDetectionListener small changes have been implemented to add simulated head positions between two real detected ones. Due to those simulated values motions of eyes are smoother, especially when lightness doesn't allow continued detection. The drawback is a short delay (100ms) used to perform the animation.

[SmoothFaceDetectionListener on GitHub](https://github.com/tvbarthel/GooglyZoo/blob/master/App/GooglyZooApp/src/main/java/fr/tvbarthel/attempt/googlyzooapp/listener/SmoothFaceDetectionListener.java)

Simple usage
=========

<pre>
  mCamera.setFaceDetectionListener(new SmoothFaceDetectionListener() {
     @Override
        public void onSmoothFaceDetection(float[] smoothFacePosition) {
           // do your stuff here
        }
     }
  );
</pre>

Supported Devices
=========
Since there is no uses-feature to filter devices which support Camera.OnFaceDetection, here is such a list.

If your device isn't listed bellow, don't hesite to make your input (= (see make your input section) 

We hope it will be useful for many of developers!

FaceDectection Available on front Camera
<pre>
  Nexus 4 | stock rom | 4.4.2
  Nexus 5 | stock rom | 4.4.2
  Nexus 5 | Cyanogenmod 11 M2 | 4.4.2 (Thanks to NoctisIgnem)
  Galaxy S4 | TouchWiz | 4.4.2
  Galaxy S3 | TouchWiz | 4.1.1
  HTC One (m7) | Revolution HD 6.1 Rom | 4.4.2 (Thanks to Eoinoc)
  HTC One (M8) | stock rom | 4.4.2 (Thanks to ThatLilChestnut)
  HTC Evo 3D | HTC Sense 3.6 | 4.0.3
  Moto G (Boost Mobile) | stock rom | 4.4.2 (Thanks to IAmJordanX)
  Moto X | stock rom | 4.4.2 (Thanks to omniuni)
  Wiko Cink Five | WiTE1 | 4.2 (Thanks to bartthebest)
</pre>

FaceDetection UnAvailable on front Camera
<pre>
  HTC one S | CyanogenMod 10.1.3-ville | 4.2.2
</pre>

Make your input
=========
Is it available on your device ? (Added in API level 14)

According to the [Official documentation](http://developer.android.com/reference/android/hardware/Camera.Parameters.html)

“If the return value is 0, face detection of the specified type is not supported.”

<pre>
  /**
  * code snippet to test if a given camera supports FaceDetectionListener
  */
  private boolean isFaceDetectionAvailable(Camera.Parameters params) {
          boolean supported = false;
          if (params.getMaxNumDetectedFaces() > 0) {
              supported = true;
          }
          return supported;
  }
</pre>

You can also use our small app, which will prompt a Toast according to the code snippet above.


TODO
=========

SmoothFaceDetectionListener : support multiple faces.

SmoothFaceDetectionListener : return face object, not only position

Googly Zoo : add capture / save / share features

Credits and License
========
Credits go to Thomas Barthélémy [https://github.com/tbarthel-fr](https://github.com/tbarthel-fr) and Vincent Barthélémy [https://github.com/vbarthel-fr](https://github.com/vbarthel-fr).

Licensed under the Beerware License:

<pre>
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy us a beer (or basically anything else) in return.
</pre>
