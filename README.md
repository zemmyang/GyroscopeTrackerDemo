# Gyroscope Tracker Demo

Basic Android app, built as an exercise: Tracks the phone's gyroscope, caches to SQLite DB, prints 20 lines to display, gives user to save to CSV file.

It's far from idiot-proof, but it works.

Saves the file to \Internal storage\Android\data\com.zemmyang.gyroscopetrackerdemo\cache, with the timestamp recorded as milliseconds since Unix epoch.

## Installation

Get APK from Releases section.

## Future?

If I were to expand this, I'd like to:
* Make it pretty, maybe have a little gyroscope animation on the cover
* Make it idiot proof, disable the "start" button once the "start" has been pressed, hide the "save CSV" file if the database has been cleared, etc.
* Send through email or other sharing options
* Record using preset times

Would be cool create a train wobbliness meter out of this, include the GPS and Accelerometer data to measure if the top half of a train is indeed wobblier than the bottom half of the train or if I'm just imagining that the train is wobblier up top...

![gif](https://user-images.githubusercontent.com/1752285/151676495-1965a8e6-784e-4542-a380-97737facc593.gif)
