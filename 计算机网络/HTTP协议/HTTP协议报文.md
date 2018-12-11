# HTTP协议报文（message）

##### HTTP报文分类

> 请求报文（resquest message）
>
> 响应报文（response message）

##### HTTP Request Message Structure

> 开始行（start-line）又叫**请求行**
>
> 首部行（headrest）
>
> 实体主体（entity-body）
>
> ![2012072810301161](http://www.runoob.com/wp-content/uploads/2013/11/2012072810301161.png)

##### HTTP Response Message Structure

> 开始行（start-line）又叫**响应行**
>
> 首部行（headrest）
>
> 实体主体（entity-body）
>
> ![httpmessage](http://www.runoob.com/wp-content/uploads/2013/11/httpmessage.jpg)

##### HTTP Request Message start-line

> HTTP请求报文开始行也叫**请求行**，由 **方法**、**[空格]**、**URL**、**[空格]**、**HTTP版本** 组成。
>
> **方法**： 向请求资源指定的资源发送请求报文的方法，其作用是可以指定请求的资源按期望产生某种行为。
> **URL** : 统一资源定位符（Uniform Resource Locator）
> **HTTP版本**：目前有 HTTP/1.0、HTTP/1.1、HTTP/2.0 版本，其中 HTTP1.0 版本使用较广泛。
>
> 以下为 HTTP/1.0 和 HTTP/1.1 支持的方法：
>
> | 方法    | 说明                 | 支持的HTTP协议版本 |
> | ------- | -------------------- | ------------------ |
> | GET     | 获取资源             | 1.0、1.1           |
> | POST    | 传输实体主体         | 1.0、1.1           |
> | PUT     | 传输文件             | 1.0、1.1           |
> | HEAD    | 获得报文首部         | 1.0、1.1           |
> | DELETE  | 删除资源             | 1.0、1.1           |
> | OPTIONS | 询问支持的方法       | 1.1                |
> | TRACE   | 追踪路径             | 1.1                |
> | CONNECT | 由于代理服务器       | 1.1                |
> | LINK    | 建立和资源之间的联系 | 1.0                |
> | UNLINK  | 断开连接关系         | 1.0                |

##### HTTP Response Message start-line

> HTTP响应报文开始行也叫**响应行**，由 **HTTP 版本**、**[空格]**、**状态码**组成。
>
> 也叫**响应行**，由 **HTTP 版本**、**[空格]**、**状态码**组成。
>
> #### 100~199信息性状态码
>
> > | Code | 原因短语            | 描述                                                         |
> > | ---- | ------------------- | ------------------------------------------------------------ |
> > | 100  | Continue            | 服务器收到请求的初始部分，请客户端继续。                     |
> > | 101  | Switching Protocols | 服务器正在根据客户端的指定，将协议更换成Update首部所列协议。 |
> >
> > 对于状态码100，起初的设计是为了：客户端想发送一个实体到服务器，但在发送之前想查看服务器是否愿意接受。需要知道的是：
> >
> > - 对于客户端，若客户端希望发送一个实体，并且愿意等待服务器状态码为100的响应，那么客户端的请求报文中应包含值为`100 Continue`的`Expect`首部。客户端在发送请求后，不要一直等待，可以在超出一定时间后直接发送实体。
> > - 对于服务器，若服务器收到包含值为`100 Continue`的首部，应该以`100 Continue`或其他对应错误状态码响应。服务器不应该向没有包含`100 Continue`首部的请求响应`100 Continue`状态码。若服务器在响应`100 Continue`状态之前已经收到客户端发送的实体部分，可以跳过响应`100 Continue`，但要响应最终的状态。
> > - 对于代理，若代理接受到客户端包含`100 Continue`的请求，在代理知道请求的下一跳只支持HTTP/1.0（或更早），它应该响应`417 Expectation Failed`；在代理知道下一跳支持HTTP/1.1或没有清楚的状态，则应转发这一请求。若代理将上游服务器响应转发给客户端时，知道客户端不支持HTTP/1.1，则不应响应`100 Continue`。
>
> #### 200~299成功状态码
>
> > 在请求成功时，服务器会返回代表成功的状态码；对于不同的请求方法，状态码有可能会有区别。已知的成功状态码如下：
> >
> > | Code | 原因短语                      | 描述                                                         |
> > | ---- | ----------------------------- | ------------------------------------------------------------ |
> > | 200  | OK                            | 最常见。请求成功，响应主体包含了具体的数据。                 |
> > | 201  | Created                       | 响应在服务器 创建资源的请求，如PUT。服务器应确保资源被创建，并在响应报文中包含资源URL。 |
> > | 202  | Accepted                      | 服务器以接收到请求，但还未执行任何操作。                     |
> > | 203  | Non-Authoritative Information | 实体首部（也可以称为元信息）包含的信息不是来自于服务器，而是资源的一个副本。若中间节点上有一份资源副本，但无法或没有对它发出的与资源有关的元信息进行验证，就会出现这种情况。当然，这种状态并不是非用不可，若实体首部来自服务器，返回200完全可以。 |
> > | 204  | No Content                    | 响应报文中无主体部分。主要用于在浏览器不转为显示新文档情况下，对其更新。 |
> > | 205  | Reset Content                 | 负责告知浏览器清除当前页面中所有HTML元素。                   |
> > | 206  | Partial Content               | 成功执行一个部分或Range请求。客户端可以在首部中指定请求某个范围内的文件。该状态响应头部必须包含Content-Range、Date、以及ETag或Content-Location。 |
>
> #### 300~399重定向状态码
>
> > | Code | 原因短语           | 描述                                                         |
> > | ---- | ------------------ | ------------------------------------------------------------ |
> > | 300  | Multiple Choices   | 客户端请求实际指向多个资源的URL。客户端可以在响应中找到资源列表。 |
> > | 301  | Moved Permanently  | 请求的URL已被移除。响应的Location首部包含现在所处的位置。    |
> > | 302  | Found              | 与301类似，客户端本次应使用响应中的临时URL，将来的请求任使用以前的URL。 |
> > | 303  | See Other          | 告知客户端使用另一个URL来获取资源。其主要目的是，允许POST请求的响应将客户端定向的某一个资源上去。 |
> > | 304  | Not Modified       | 若客户端发起一个有条件的GET请求，而资源未被修改，可以使用该状态码说明资源未被修改。 |
> > | 305  | Use Proxy          | 必须通过代理来访问这一资源，代理有Location首部给出。需要知道的是，客户端接收到这一状态时，不应该假定所有请求都经过代理。 |
> > | 306  | 未使用             | 暂未使用。                                                   |
> > | 307  | Temporary Redirect | 和302相同。                                                  |
> >
> > 对于`302`、`303`、`307`状态码的说明：从上面表格上看，这3个状态码出现交叉的情况；在HTTP/1.0，只有`302`，服务器希望对POST请求响应`302`后，客户端向从定向的URL发送GET请求。`303`、`307`是在HTTP/1.1加入，`303`时，浏览器依然执行HTTP/1.0 `302`的动作；`307`，只是不会将原始的POST转为GET，而是询问用户。这些都是规范说辞，但实际运用中不是这么回事，你有看到大量的`307`？
>
> #### 400~499客户端错误状态码
>
> > 有时客户端发送服务器无法处理的东西，会导致错误。 已知状态码列表：
> >
> > | Code | 原因短语                        | 描述                                                         |
> > | ---- | ------------------------------- | ------------------------------------------------------------ |
> > | 400  | Bad Request                     | 告知客户端它发送了一个错误的请求。                           |
> > | 401  | Unauthorized                    | 与适当首部一同返回，告知客户端在请求之前先进行认证。         |
> > | 402  | Payment Required                | 保留未使用。                                                 |
> > | 403  | Forbidden                       | 请求被拒绝。                                                 |
> > | 404  | Not Found                       | 服务器无法找到请求的URL。                                    |
> > | 405  | Method Not Allowed              | 客户端使用不支持的方法请求URL。应该在首部使用Allow告知客户端正确的方法。 |
> > | 406  | Not Acceptable                  | 客户端在使用指定参数说明其愿意接收什么类型的实体，但服务器没有与之对应的资源。 |
> > | 407  | Proxy Authentication Required   | 代理服务器要求客户端验证。                                   |
> > | 408  | Request Timeout                 | 客户端完成请求时间过长，服务器可以关闭链接。                 |
> > | 409  | Conflict                        | 服务器认为该请求可能引起冲突。响应主体中应包含冲突的主体的描述。 |
> > | 410  | Gone                            | 与404类似，只是服务器曾经拥有此资源，后来被移除。            |
> > | 411  | Length Required                 | 服务器要求请求报文中包含Content-Length首部。                 |
> > | 412  | Precondition Failed             | 客户端发起条件请求，其中有条件失败。                         |
> > | 413  | Request Entity Too Large        | 客户端发送的主体部分比服务器能够活希望处理的要大。           |
> > | 414  | Request URI Too Long            | URL过长。                                                    |
> > | 415  | Unsupported Media Type          | 服务器无法理解或无法支持客户端发送的内容类型。               |
> > | 416  | Requested Range Not Satisfiable | 请求范围无效或无法满足。                                     |
> > | 417  | Expectation Failed              | 请求首部包含Expect期望，但服务器无法满足。                   |
>
> #### 500~599服务器错误状态码
>
> > | Code | 原因短语                 | 描述                                                         |
> > | ---- | ------------------------ | ------------------------------------------------------------ |
> > | 500  | Internal Server Error    | 服务器遇到一个妨碍它提供服务的错误。                         |
> > | 501  | Not Implemented          | 客户端发起的请求超出服务器能力范围，如使用了不支持的方法。   |
> > | 502  | Bad Gateway              | 无效网关。通常不是这上游服务器关闭，而是使用了上游服务器不同意协议交换数据。 |
> > | 503  | Service Unavailable      | 服务器暂时无法提供服务。若服务器知道服务什么时间可以使用，可以在响应头中加入Retry-After首部说明。 |
> > | 504  | Gateway Timeout          | 于408类似，只是这里的响应来自一个网关或代理，它们在等待另一个服务器响应对其请求响应时超时。 |
> > | 505  | HTTP Version Not Support | 服务器收到的请求使用了它无法支持的协议版本。                 |
>
> #### 自定义状态码
>
> > 在做服务器端开发的时候，服务器端响应客户端的状态码是可以自定义的，具体的状态码的理解需要两者之间的沟通。比如前端发送一个创建用户的请求，后端发现该用户名已经存在，返回一个409状态码，这个409就是前后端自己协商的含义。

##### HTTP Request Message headers

>* 用来说明浏览器、服务器或报文主体的一些信息。
>* 可以有好几行，也可以不使用
>* 每个首部行都是由 **首部字段名**、**[空格]** 和 **值** 组成
>* 每个首部行在结束地方都有 CRLF（『回车』和『换行』符）
>
>HTTP 首部字段分为 4 种： **通用首部字段**、**请求首部字段**、**响应首部字段**、**实体首部字段**。
>
>![img](https://upload-images.jianshu.io/upload_images/310976-40f0e13165a53469.png)

##### HTTP Message entity-body

> Note that the term "HTTP entity" no longer appears in the [latest HTTP 1.1 specifications](http://tools.ietf.org/html/rfc7230). Looks like it's been deprecated. Now we can just use "header fields" and "message body"
>
> #### HTTP Content-type
>
> > 常用的
> >
> > - application/x-www-form-urlencoded
> > - multipart/form-data
> > - application/json
> > - application/xml
>
> | 文件扩展名                          | Content-Type(Mime-Type)                 | 文件扩展名 | Content-Type(Mime-Type)             |
> | ----------------------------------- | --------------------------------------- | ---------- | ----------------------------------- |
> | .*（ 二进制流，不知道下载文件类型） | application/octet-stream                | .tif       | image/tiff                          |
> | .001                                | application/x-001                       | .301       | application/x-301                   |
> | .323                                | text/h323                               | .906       | application/x-906                   |
> | .907                                | drawing/907                             | .a11       | application/x-a11                   |
> | .acp                                | audio/x-mei-aac                         | .ai        | application/postscript              |
> | .aif                                | audio/aiff                              | .aifc      | audio/aiff                          |
> | .aiff                               | audio/aiff                              | .anv       | application/x-anv                   |
> | .asa                                | text/asa                                | .asf       | video/x-ms-asf                      |
> | .asp                                | text/asp                                | .asx       | video/x-ms-asf                      |
> | .au                                 | audio/basic                             | .avi       | video/avi                           |
> | .awf                                | application/vnd.adobe.workflow          | .biz       | text/xml                            |
> | .bmp                                | application/x-bmp                       | .bot       | application/x-bot                   |
> | .c4t                                | application/x-c4t                       | .c90       | application/x-c90                   |
> | .cal                                | application/x-cals                      | .cat       | application/vnd.ms-pki.seccat       |
> | .cdf                                | application/x-netcdf                    | .cdr       | application/x-cdr                   |
> | .cel                                | application/x-cel                       | .cer       | application/x-x509-ca-cert          |
> | .cg4                                | application/x-g4                        | .cgm       | application/x-cgm                   |
> | .cit                                | application/x-cit                       | .class     | java/*                              |
> | .cml                                | text/xml                                | .cmp       | application/x-cmp                   |
> | .cmx                                | application/x-cmx                       | .cot       | application/x-cot                   |
> | .crl                                | application/pkix-crl                    | .crt       | application/x-x509-ca-cert          |
> | .csi                                | application/x-csi                       | .css       | text/css                            |
> | .cut                                | application/x-cut                       | .dbf       | application/x-dbf                   |
> | .dbm                                | application/x-dbm                       | .dbx       | application/x-dbx                   |
> | .dcd                                | text/xml                                | .dcx       | application/x-dcx                   |
> | .der                                | application/x-x509-ca-cert              | .dgn       | application/x-dgn                   |
> | .dib                                | application/x-dib                       | .dll       | application/x-msdownload            |
> | .doc                                | application/msword                      | .dot       | application/msword                  |
> | .drw                                | application/x-drw                       | .dtd       | text/xml                            |
> | .dwf                                | Model/vnd.dwf                           | .dwf       | application/x-dwf                   |
> | .dwg                                | application/x-dwg                       | .dxb       | application/x-dxb                   |
> | .dxf                                | application/x-dxf                       | .edn       | application/vnd.adobe.edn           |
> | .emf                                | application/x-emf                       | .eml       | message/rfc822                      |
> | .ent                                | text/xml                                | .epi       | application/x-epi                   |
> | .eps                                | application/x-ps                        | .eps       | application/postscript              |
> | .etd                                | application/x-ebx                       | .exe       | application/x-msdownload            |
> | .fax                                | image/fax                               | .fdf       | application/vnd.fdf                 |
> | .fif                                | application/fractals                    | .fo        | text/xml                            |
> | .frm                                | application/x-frm                       | .g4        | application/x-g4                    |
> | .gbr                                | application/x-gbr                       | .          | application/x-                      |
> | .gif                                | image/gif                               | .gl2       | application/x-gl2                   |
> | .gp4                                | application/x-gp4                       | .hgl       | application/x-hgl                   |
> | .hmr                                | application/x-hmr                       | .hpg       | application/x-hpgl                  |
> | .hpl                                | application/x-hpl                       | .hqx       | application/mac-binhex40            |
> | .hrf                                | application/x-hrf                       | .hta       | application/hta                     |
> | .htc                                | text/x-component                        | .htm       | text/html                           |
> | .html                               | text/html                               | .htt       | text/webviewhtml                    |
> | .htx                                | text/html                               | .icb       | application/x-icb                   |
> | .ico                                | image/x-icon                            | .ico       | application/x-ico                   |
> | .iff                                | application/x-iff                       | .ig4       | application/x-g4                    |
> | .igs                                | application/x-igs                       | .iii       | application/x-iphone                |
> | .img                                | application/x-img                       | .ins       | application/x-internet-signup       |
> | .isp                                | application/x-internet-signup           | .IVF       | video/x-ivf                         |
> | .java                               | java/*                                  | .jfif      | image/jpeg                          |
> | .jpe                                | image/jpeg                              | .jpe       | application/x-jpe                   |
> | .jpeg                               | image/jpeg                              | .jpg       | image/jpeg                          |
> | .jpg                                | application/x-jpg                       | .js        | application/x-javascript            |
> | .jsp                                | text/html                               | .la1       | audio/x-liquid-file                 |
> | .lar                                | application/x-laplayer-reg              | .latex     | application/x-latex                 |
> | .lavs                               | audio/x-liquid-secure                   | .lbm       | application/x-lbm                   |
> | .lmsff                              | audio/x-la-lms                          | .ls        | application/x-javascript            |
> | .ltr                                | application/x-ltr                       | .m1v       | video/x-mpeg                        |
> | .m2v                                | video/x-mpeg                            | .m3u       | audio/mpegurl                       |
> | .m4e                                | video/mpeg4                             | .mac       | application/x-mac                   |
> | .man                                | application/x-troff-man                 | .math      | text/xml                            |
> | .mdb                                | application/msaccess                    | .mdb       | application/x-mdb                   |
> | .mfp                                | application/x-shockwave-flash           | .mht       | message/rfc822                      |
> | .mhtml                              | message/rfc822                          | .mi        | application/x-mi                    |
> | .mid                                | audio/mid                               | .midi      | audio/mid                           |
> | .mil                                | application/x-mil                       | .mml       | text/xml                            |
> | .mnd                                | audio/x-musicnet-download               | .mns       | audio/x-musicnet-stream             |
> | .mocha                              | application/x-javascript                | .movie     | video/x-sgi-movie                   |
> | .mp1                                | audio/mp1                               | .mp2       | audio/mp2                           |
> | .mp2v                               | video/mpeg                              | .mp3       | audio/mp3                           |
> | .mp4                                | video/mpeg4                             | .mpa       | video/x-mpg                         |
> | .mpd                                | application/vnd.ms-project              | .mpe       | video/x-mpeg                        |
> | .mpeg                               | video/mpg                               | .mpg       | video/mpg                           |
> | .mpga                               | audio/rn-mpeg                           | .mpp       | application/vnd.ms-project          |
> | .mps                                | video/x-mpeg                            | .mpt       | application/vnd.ms-project          |
> | .mpv                                | video/mpg                               | .mpv2      | video/mpeg                          |
> | .mpw                                | application/vnd.ms-project              | .mpx       | application/vnd.ms-project          |
> | .mtx                                | text/xml                                | .mxp       | application/x-mmxp                  |
> | .net                                | image/pnetvue                           | .nrf       | application/x-nrf                   |
> | .nws                                | message/rfc822                          | .odc       | text/x-ms-odc                       |
> | .out                                | application/x-out                       | .p10       | application/pkcs10                  |
> | .p12                                | application/x-pkcs12                    | .p7b       | application/x-pkcs7-certificates    |
> | .p7c                                | application/pkcs7-mime                  | .p7m       | application/pkcs7-mime              |
> | .p7r                                | application/x-pkcs7-certreqresp         | .p7s       | application/pkcs7-signature         |
> | .pc5                                | application/x-pc5                       | .pci       | application/x-pci                   |
> | .pcl                                | application/x-pcl                       | .pcx       | application/x-pcx                   |
> | .pdf                                | application/pdf                         | .pdf       | application/pdf                     |
> | .pdx                                | application/vnd.adobe.pdx               | .pfx       | application/x-pkcs12                |
> | .pgl                                | application/x-pgl                       | .pic       | application/x-pic                   |
> | .pko                                | application/vnd.ms-pki.pko              | .pl        | application/x-perl                  |
> | .plg                                | text/html                               | .pls       | audio/scpls                         |
> | .plt                                | application/x-plt                       | .png       | image/png                           |
> | .png                                | application/x-png                       | .pot       | application/vnd.ms-powerpoint       |
> | .ppa                                | application/vnd.ms-powerpoint           | .ppm       | application/x-ppm                   |
> | .pps                                | application/vnd.ms-powerpoint           | .ppt       | application/vnd.ms-powerpoint       |
> | .ppt                                | application/x-ppt                       | .pr        | application/x-pr                    |
> | .prf                                | application/pics-rules                  | .prn       | application/x-prn                   |
> | .prt                                | application/x-prt                       | .ps        | application/x-ps                    |
> | .ps                                 | application/postscript                  | .ptn       | application/x-ptn                   |
> | .pwz                                | application/vnd.ms-powerpoint           | .r3t       | text/vnd.rn-realtext3d              |
> | .ra                                 | audio/vnd.rn-realaudio                  | .ram       | audio/x-pn-realaudio                |
> | .ras                                | application/x-ras                       | .rat       | application/rat-file                |
> | .rdf                                | text/xml                                | .rec       | application/vnd.rn-recording        |
> | .red                                | application/x-red                       | .rgb       | application/x-rgb                   |
> | .rjs                                | application/vnd.rn-realsystem-rjs       | .rjt       | application/vnd.rn-realsystem-rjt   |
> | .rlc                                | application/x-rlc                       | .rle       | application/x-rle                   |
> | .rm                                 | application/vnd.rn-realmedia            | .rmf       | application/vnd.adobe.rmf           |
> | .rmi                                | audio/mid                               | .rmj       | application/vnd.rn-realsystem-rmj   |
> | .rmm                                | audio/x-pn-realaudio                    | .rmp       | application/vnd.rn-rn_music_package |
> | .rms                                | application/vnd.rn-realmedia-secure     | .rmvb      | application/vnd.rn-realmedia-vbr    |
> | .rmx                                | application/vnd.rn-realsystem-rmx       | .rnx       | application/vnd.rn-realplayer       |
> | .rp                                 | image/vnd.rn-realpix                    | .rpm       | audio/x-pn-realaudio-plugin         |
> | .rsml                               | application/vnd.rn-rsml                 | .rt        | text/vnd.rn-realtext                |
> | .rtf                                | application/msword                      | .rtf       | application/x-rtf                   |
> | .rv                                 | video/vnd.rn-realvideo                  | .sam       | application/x-sam                   |
> | .sat                                | application/x-sat                       | .sdp       | application/sdp                     |
> | .sdw                                | application/x-sdw                       | .sit       | application/x-stuffit               |
> | .slb                                | application/x-slb                       | .sld       | application/x-sld                   |
> | .slk                                | drawing/x-slk                           | .smi       | application/smil                    |
> | .smil                               | application/smil                        | .smk       | application/x-smk                   |
> | .snd                                | audio/basic                             | .sol       | text/plain                          |
> | .sor                                | text/plain                              | .spc       | application/x-pkcs7-certificates    |
> | .spl                                | application/futuresplash                | .spp       | text/xml                            |
> | .ssm                                | application/streamingmedia              | .sst       | application/vnd.ms-pki.certstore    |
> | .stl                                | application/vnd.ms-pki.stl              | .stm       | text/html                           |
> | .sty                                | application/x-sty                       | .svg       | text/xml                            |
> | .swf                                | application/x-shockwave-flash           | .tdf       | application/x-tdf                   |
> | .tg4                                | application/x-tg4                       | .tga       | application/x-tga                   |
> | .tif                                | image/tiff                              | .tif       | application/x-tif                   |
> | .tiff                               | image/tiff                              | .tld       | text/xml                            |
> | .top                                | drawing/x-top                           | .torrent   | application/x-bittorrent            |
> | .tsd                                | text/xml                                | .txt       | text/plain                          |
> | .uin                                | application/x-icq                       | .uls       | text/iuls                           |
> | .vcf                                | text/x-vcard                            | .vda       | application/x-vda                   |
> | .vdx                                | application/vnd.visio                   | .vml       | text/xml                            |
> | .vpg                                | application/x-vpeg005                   | .vsd       | application/vnd.visio               |
> | .vsd                                | application/x-vsd                       | .vss       | application/vnd.visio               |
> | .vst                                | application/vnd.visio                   | .vst       | application/x-vst                   |
> | .vsw                                | application/vnd.visio                   | .vsx       | application/vnd.visio               |
> | .vtx                                | application/vnd.visio                   | .vxml      | text/xml                            |
> | .wav                                | audio/wav                               | .wax       | audio/x-ms-wax                      |
> | .wb1                                | application/x-wb1                       | .wb2       | application/x-wb2                   |
> | .wb3                                | application/x-wb3                       | .wbmp      | image/vnd.wap.wbmp                  |
> | .wiz                                | application/msword                      | .wk3       | application/x-wk3                   |
> | .wk4                                | application/x-wk4                       | .wkq       | application/x-wkq                   |
> | .wks                                | application/x-wks                       | .wm        | video/x-ms-wm                       |
> | .wma                                | audio/x-ms-wma                          | .wmd       | application/x-ms-wmd                |
> | .wmf                                | application/x-wmf                       | .wml       | text/vnd.wap.wml                    |
> | .wmv                                | video/x-ms-wmv                          | .wmx       | video/x-ms-wmx                      |
> | .wmz                                | application/x-ms-wmz                    | .wp6       | application/x-wp6                   |
> | .wpd                                | application/x-wpd                       | .wpg       | application/x-wpg                   |
> | .wpl                                | application/vnd.ms-wpl                  | .wq1       | application/x-wq1                   |
> | .wr1                                | application/x-wr1                       | .wri       | application/x-wri                   |
> | .wrk                                | application/x-wrk                       | .ws        | application/x-ws                    |
> | .ws2                                | application/x-ws                        | .wsc       | text/scriptlet                      |
> | .wsdl                               | text/xml                                | .wvx       | video/x-ms-wvx                      |
> | .xdp                                | application/vnd.adobe.xdp               | .xdr       | text/xml                            |
> | .xfd                                | application/vnd.adobe.xfd               | .xfdf      | application/vnd.adobe.xfdf          |
> | .xhtml                              | text/html                               | .xls       | application/vnd.ms-excel            |
> | .xls                                | application/x-xls                       | .xlw       | application/x-xlw                   |
> | .xml                                | text/xml                                | .xpl       | audio/scpls                         |
> | .xq                                 | text/xml                                | .xql       | text/xml                            |
> | .xquery                             | text/xml                                | .xsd       | text/xml                            |
> | .xsl                                | text/xml                                | .xslt      | text/xml                            |
> | .xwd                                | application/x-xwd                       | .x_b       | application/x-x_b                   |
> | .sis                                | application/vnd.symbian.install         | .sisx      | application/vnd.symbian.install     |
> | .x_t                                | application/x-x_t                       | .ipa       | application/vnd.iphone              |
> | .apk                                | application/vnd.android.package-archive | .xap       | application/x-silverlight-app       |