# Map a sensor state to a specified Function on a specified loco address
#
# used at MWOT to let visitors control the horn and lights on locos, via fascia buttons 
#
# Author: Steve Todd, copyright 2014
# Part of the JMRI distribution
#
# from sm2 import Main
# from myscene import listenerObjects
from Adaption import test1
import java
import jmri
# import time

def test():
    x=test1()
    x.myfun(java.lang.Integer(3))


# if __name__ == '__main__':
#     print "Executing the file"
test()
