'''
Created on 20 Feb 2016

@author: bill
'''


from abc import ABCMeta, abstractmethod
 
class Observer(object):
    __metaclass__ = ABCMeta
 
    @abstractmethod
    def update(self, *args, **kwargs):
        pass