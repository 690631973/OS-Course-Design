# library
javafx

# concurrent

## Task 
use `import javafx.concurrent.Task `

Task is similar with `Runnable`, which can be used to init a Thread.
It is specially designed for javafx GUI, because
In javafx, all updates of UI elements must be applied on
main javafx thread, for thread safety's sake.
but in some cases, like this course design, We must update UI elements
in an other thread. 
For example, in this course design. each instruction of a process should
be observed clearly by the user. To achieve this, we made the process
sleep for a while before executes next instruction, If process runs on
the main javafx thread, the GUI will also be stopped, until no more
sleep on any preocess. So we must made process running on other thread.
Task can make it possible with thread safety.
So how Task work?
it taskes advantage of java bean's property class and bind method.
property is a wrapper for a vairiable , you can bind two properties,
if any one of the two's value is changed, the another will change 
either. Task has some builtin properties to bind, 
and UI elements also have builtin properties waiting for binding.
you can bind them, change a property on the Task's Thread,
but made UI element's property , which is safely on javafx thread, changes too.

## Platform.RunLater

In some cases, directly change variables on fx thread from another thread is necessary
`Platform.RunLater` poses a synchronization between two threads, 
to ensure thread safety.



