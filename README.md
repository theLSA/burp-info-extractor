# burp-info-extractor
## 概述

提取HTTP响应数据包中的信息，如用户名，密码等。

比如一个api（/user/list）返回大量用户名密码，大多数是json格式（jsonarray），就可以使用此工具快速提取信息。

解析json数据使用的是google gson。

## 快速开始

采用两种提取方式，以二层嵌套json格式的响应数据为例：

{“users”:[{“username”:”tom”},......}

1）json格式提取：users-username

2）正则提取："username":"(.*?)"

![](https://github.com/theLSA/burp-info-extractor/raw/master/demo/bie00.png)

测试了

一层json嵌套

[{“username”:”tom”},......]

二层json嵌套

{“users”:[{“username”:”tom”},......}

三层json嵌套

{"code":0,"msg":"\u6210\u529f","data":{"current_page":1,"users":[{"unique_id":"9e4147f6e41bb457bcf75227fd646e8e","username":"tom"},......]}}

皆能完美提取username 

注意大小写敏感！

## 反馈

[issues](https://github.com/theLSA/burp-info-extractor/issues)
