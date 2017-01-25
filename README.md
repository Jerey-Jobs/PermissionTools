# PermissionTools

PermissionTools is a library to simplify basic system permissions logic when targeting Android M or higher<br>
PermissionTools一个用于Android权限申请的工具库，当你的应用target版本为23或者更高时才能使用

-------------------

## import/引入

project's build.gradle (工程下的 build.gradle)

``` gradle
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://jerey.bintray.com/maven"
        }
    }
}
```

module's build.gradle (模块的build.gradle)

``` gradle
dependencies {
    compile 'com.cn.jerey:permissiontools:1.3'
}
```

## Usage/用法

you can use it like this：

``` java
  PermissionTools permissionTools;

  permissionTools =  new PermissionTools.Builder(this)
                        .setOnPermissionCallbacks(new PermissionCallbacks() {
                            @Override
                            public void onPermissionsGranted(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请通过",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionsDenied(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请被拒绝",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setRequestCode(111)
                        .build();
  permissionTools.requestPermissions(Manifest.permission.CAMERA); 
```

and in onRequestPermissionsResult

``` java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionTools.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
```

it's so easy!

-----------------------

## Why this library ?

To help developer request the permissions easily.

##License

```
Copyright 2014-2016 lypeer.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
