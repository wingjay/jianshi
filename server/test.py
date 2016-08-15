import leancloud

leancloud.init("eC8KzqHkXC3wgmaoYNV4fAO9-gzGzoHsz", "xaretW87UsjcgdIYujwbkyWy")

Todo = leancloud.Object.extend('Todo')
test_object = Todo()
test_object.set('words', "Hello World!")
test_object.save()
