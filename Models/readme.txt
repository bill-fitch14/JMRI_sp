
Chapter 7. Walking Around the Models

From:
  Pro Java 6 3D Game Development
  Andrew Davison
  Apress, April 2007
  ISBN: 1590598172 
  http://www.apress.com/book/bookDisplay.html?bID=10256
  Web Site for the book: http://fivedots.coe.psu.ac.th/~ad/jg2


Contact Address:
  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat Yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the book's Web site.

Thanks,
  Andrew


==================================
Models/    

A directory holding the 3D OBJ models used by ModelLoader
in ObjView3D.

There are 6 model here, with additional MTL and image files:

barbell.obj
 - a barbell
 - constructed from three groups of vertices representing the 
   balls and the crossbeam. Colors for the balls are defined 
   inside barbell.mtl.

colorCube.obj
 - a colored cube
 - a simple OBJ model created manually by me. The faces are 
   red or green, with the colors specified in colorCube.mtl.

heli.obj
 - a helicopter 
 - constructed from multiple groups, with a texture assigned to 
   its cockpit (as defined in heli.mtl and metal.gif)

humanoid.obj
 - a figure
 - consists of a single unnamed group of vertices, colored with 
   the "flesh" Java3D built-in material

longBox.obj
 - a box
 - wrapped around with a texture of my son John 
  (specified in longBox.mtl and john.jpg)

penguin.obj
 - a penguin
 - utilizes a texture (specified in penguin.mtl and penguin.gif)

-----------
Last updated: 3rd March 2007