GooglyZoo
=========

![Googly Zoo banner](http://tvbarthel.github.io/images/googly.png)

Googly Zoo is an augmented virtuality experience. Choose your favorite virtual Googly pet and simply move your face in front of your smartphone to wake it up. Once your virtual pet is awake, its Googly Eyes will track you for hours without blinking! Even if your new virtual pet doesn't show any signs of tiredness, it will automatically fall asleep as soon as you disappear from your screen.

[Short video on YouTube](https://www.youtube.com/watch?v=hu4kZRP5mZA)

[Become a beta tester](https://plus.google.com/101426573110982697753/posts/5vQ9rJWFFQk)

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

TODO
=========

SmoothFaceDetectionListener : support multiple faces.
SmoothFaceDetectionListener : return face object, not only position
Googly Zoo : add some interactions 

Credits and License
========
Credits go to Thomas Barthélémy [https://github.com/tbarthel-fr](https://github.com/tbarthel-fr) and Vincent Barthélémy [https://github.com/vbarthel-fr](https://github.com/vbarthel-fr).

Licensed under the Beerware License:

<pre>
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy us a beer (or basically anything else) in return.
</pre>
