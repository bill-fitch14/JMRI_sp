@echo off
echo Converting sky BMPs to JPEGs

nconvert -quiet -out jpeg -q 92 -o skyFront.jpg sky0001.bmp 

nconvert -quiet -out jpeg -q 92 -o skyRight.jpg sky0002.bmp 

nconvert -quiet -out jpeg -q 92 -o skyBack.jpg sky0003.bmp 

nconvert -quiet -out jpeg -q 92 -o skyLeft.jpg sky0004.bmp 

nconvert -quiet -out jpeg -q 92 -o skyAbove.jpg sky0005.bmp 

nconvert -quiet -out jpeg -q 92 -o floor.jpg sky0006.bmp 

echo Finished.