
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
skyBox/    

A directory holding textures used for the background
in ObjView3D.

There are three different background techniques used in
ObjView3D:

1. A textured-wrapped sphere.

2. A skybox (a Java 3D Box with textures pasted onto each face).

3. A skybox covered with Terragen-generated textures. This version is built 
   from quads, which gives the programmer more control over how it's positioned. 
   Terragen is a freeware photorealistic landscape renderer 
   (available at http://www.planetside.co.uk/terragen).


The images are used with the different background techniques:

1. A textured-wrapped sphere.
      - lava.jpg, stars.jpg, or clouds.jpg


2. A skybox 
      - stars.jpg or clouds.jpg


3. A skybox covered with Terragen-generated textures.
   
   For details on how to create six BMPs for the skybox faces, read
   http://developer.valvesoftware.com/wiki/Creating_a_2D_skybox_with_Terragen
   However, use **my** Terragen script, not theirs:
      skybox.tgs

   It creates sky0001.bmp -- sky0006.bmp in the c:\ directory. This
   directory can be changed by editing skybox.tgs.

   The BMPs should be converted to JPGs. I use the batch file:
      skyConverter.bat

   which uses the command line image manipulation tool, nconvert,
   available from http://perso.orange.fr/pierre.g/xnview/en_nconvert.html

   It generates:
       skyFront.jpg, skyRight.jpg, skyBack.jpg, 
       skyLeft.jpg, skyAbove.jpg, floor.jpg

   They must be stored in this directory (skyBox/) for ObjView3D to
   find them at load time.

-----------
Last updated: 3rd March 2007