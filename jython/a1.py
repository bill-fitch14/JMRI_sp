'''
Created on 20 Feb 2016

@author: bill
'''


from sm2 import Main
import java
import jmri

# First, define the listener.  This one just prints some
# information on the change, but more complicated code is
# of course possible.
class MyListener(java.beans.PropertyChangeListener):
  def propertyChange(self, event):
    print "change",event.propertyName
    print "from", event.oldValue, "to", event.newValue

    #print "source systemName", event.source.systemName
    #print "source userName", event.source.userName

# Second, attach that listener to a particular turnout. The variable m
# is used to remember the listener so we can remove it later
#t = turnouts.provideTurnout("12")
p1 = Main.lo
m = MyListener()
p1.addPropertyChangeListener(m)
Main.runFSM()