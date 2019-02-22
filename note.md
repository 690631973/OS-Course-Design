# data structure not match
To show up in frontend , I need `PageTable` like this.
``ListView<PageTable> pagetable = new ListView();``
But my partner give an other.

# class conflict
For specail frontend purpose, I made a trick to pass by a problem
But my partner knows no nothing about this.
His class `MemoryManager` made me feel difficult to merge into 
existing code. 
For example 
In `Process` , `exec()` -> `sto()` -> `Memory`
In my design, `Pros` all be scheduled by `duler`, 
But he has a `MemoryManager`, it controls all memory, made me hard.

# class not compatiable

For example, `ListView` needs `setItems(items)`
but `items` must be `ObservableList<T?>`,
If I need to show `PageTable`, I need A List of class,
but my partner  offers a class, containing A list.

# character messy
czxt(1).java:274: 错误: 编码UTF8的不可映射字符
		System.out.println("ҳ��:"+outpagenum[0]+"ҳ֡:"+outpagenum[1]+"���滻");
		
charSet different utf8 , GBK


# threads safety 
In backend , we must use some `Platform.runLater` 
to simulate concurrency and assure thread safety,
But without frontend knowledge, you do not know where to insert this.

# 并行
因为我们要模拟一个并发环境
我的设计是: 在scheduler中,每隔1秒,运行一次sched(),这个1秒是通过Thread.sleep(1000)实现的, 该线程休眠,但是UI线程还在继续,看起来就像是间隔了1秒
然后一个进程的指令, 运行的是非常快的,看都看不清,为了让用户看清楚指令的运行,
必须放慢速度,每次指令运行之后,进程都要sleep 1秒,
如果不用Platform.runLater, UI线程就会卡住, 不仅仅是一动不动,而且对用户输入没有响应,
严重的话会整个变白,
这是因为我们对UI的更新通过Task类,被强行转移到UI线程,而不是后端的运算线程,
作为一个后来者,这部分计算必须被延迟,才能保证UI线程自身的正常工作


