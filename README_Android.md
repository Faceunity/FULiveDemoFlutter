# 整体目录结构介绍 



![](E:\thrid part sdk demo\FULiveDemoFlutter\tree.png)

## FULiveDemoFlutter 工程壳根目录

### fulive_flutter: Flutter 工程的根目录

#### build: 编译产物

#### fulive_plugin: native 通信的插件目录

#### ios:  在ios平台上运行Flutter 代码的壳工程

#### android：在android 平台上运行的Flutter 代码壳工程

#### lib: Flutter 主要代码

#### resource: 资源文件 

#### pubspec.lock：了解过cocoapod都知道，锁定当前pub依赖对应的版本文件，类似 podFile.lock作用

#### pubspec.yaml: Flutter 依赖文件

#### Flutter_script_iOS.sh 脚本初始化Flutter 处理Flutter 项目运行的环境目前只针对iOS平台

----

![](E:\thrid part sdk demo\FULiveDemoFlutter\fulive_flutter\lib\moduleTree.png)

### lib:  dart 模块根目录

#### BaseModule :  页面基础模块构成

#### Beauty: 美颜模块

#### Main: app 启动的主页面

#### Makeup: 美妆模块

#### Tools: 工具类

#### main.dart: native 调用Flutter 的入口模块

----

![](E:\thrid part sdk demo\FULiveDemoFlutter\fulive_flutter\fulive_plugin\pluginTree.png)

### fulive_plugin: 插件根目录

#### Android： android native 插件代码

#### ios: iOS native 插件代码

#### lib:  连接 dart 和 对应的naitve 平台插件的中间层或者理解为 平台插件接口层

#### pubspec.lock 和 pubspec.yaml 前面已经解释过

# 项目怎样跑起来(Android Studio篇)

0. Android Studio必须安装 flutter插件和dart插件

1. 使用Android Studio 打开项目根目录下的 fulive_flutter

2. 找到并打开pubspec.yaml ,右上角应该会出现快捷按钮 Get dependencies,单击后AS将自动执行 --no-color pub get, 完成后项目目录中将出现自动生成的 .android 文件夹

   也可以用控制台打开pubspec.yaml 的位置,手动执行 --no-color pub get

3. 在AS左侧目录中找到 fulive_plugin/android 文件夹 右键点击打开Flutter -> Open Android module in Android Studio


![android](E:\thrid part sdk demo\FULiveDemoFlutter\android.png)

4. 等AS index 完就可以运行和编辑了
