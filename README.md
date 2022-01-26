# 3 Android apps written in Kotlin
## [lab1](lab1) is a calculator app.
### Features
- Basic calculator with the ability to enter numbers, dots, brackets, multiplication signs, addition, division, subtraction, as well as deleting the previous character (depending on the position of the cursor), clearing the input-output field and calculating the entered expression.
- It is possible to move the cursor and enter text from any position in the current input field.
- Also supports entering expressions with brackets of any nesting.
- Extended block with the ability to enter the function of sine, cosine and module number (%) and exponentiation.
The extended block is initially hidden, it is called / hidden when you click on the floating button at the top of the screen. Implemented via a fragment.
Support for portrait and landscape modes, when switching from one to another, the value in the input field is saved.
- "The balance of points in a number". You can't enter more than one point in a number, regardless of the number of fractional numbers in the expression.
- Replacement of arithmetic operators with similar actions, i.e. if you enter a "-" sign after the "+" sign, the minus sign will replace the plus sign.
- Support for Russian and English (default) localization.
- [Keval](https://github.com/notKamui/Keval) library was used to evaluate mathematic expressions
### Design
With extended block hidden:

![Alt-text](https://i.ibb.co/YL6f7B8/unna1med.jpg)

With extended block shown:

![Alt-text](https://i.ibb.co/9qfkkyB/unnamed.jpg)

In landscape mode:

![Alt-text](https://i.ibb.co/ncq7LJc/g.jpg)

## [lab2](lab2) is an app to display and filter enrollees list.
### Features
- Display enrollees' info and calculated average grade on the main page
- Loading enrollees from json file on the device
- Filtering enrollees whose average grade is higher than 4.5
- Search bar for enrollees' cities
- Count the enrollees with avg grade > 4.5
- Edit/Delete context menu
- Ability to add new enrollees via creation form
- Support for Russian and English (default) localization

## [lab3](lab3) is a media player app.
### Features
- Autoload of all music and video files on the device
- Separate tabs for music and video lists on the main page
- Play/pause, next/previous track, controllable track progress bar which is auto updated in a separate thread, skipping back/forth for 10 secs, usage of swipes to play next and previous tracks
- Fullscreen mode for videos
- Synchronised player notification with playing controls and progress bar
- Background listening
