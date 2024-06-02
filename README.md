# burp-info-extractor
## 概述

快速提取数据中有价值的信息。

比如一个API（/user/list）返回大量用户名/密码，就可以使用此burpsuite插件快速提取。

关于该插件的实现细节，参考[burpsuite插件开发总结](https://www.lsablog.com/networksec/penetration/burpsuite-plugin-development/)

## 快速开始

支持右键send rsp to BIE直接发送HTTP响应包到本插件，也可以直接粘贴响应包或数据到插件文本框。

采用两种提取方式：

1）json格式提取：users-username

json格式提取使用了google gson解析json数据，最多支持3层json嵌套，可以对付大多数情况了。

![](https://github.com/theLSA/burp-info-extractor/raw/master/demo/bie03.png)

![](https://github.com/theLSA/burp-info-extractor/raw/master/demo/bie00.png)

2）正则提取："username":"(.*?)"

![](https://github.com/theLSA/burp-info-extractor/raw/master/demo/bie02.png)

json格式的数据测试了如下：

一层json嵌套：

[{“username”:”tom”},......]

二层json嵌套：

{“users”:[{“username”:”tom”},......}

三层json嵌套：

{"code":0,"msg":"\u6210\u529f","data":{"current_page":1,"users":[{"unique_id":"9e4147f6e41bb457bcf75227fd646e8e","username":"tom"},......]}}

皆能完美提取username 

注意大小写敏感！

## 反馈

[issues](https://github.com/theLSA/burp-info-extractor/issues)
