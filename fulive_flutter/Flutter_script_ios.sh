echo "flutter clean"
flutter clean 

echo "flutter pub get"
flutter pub get
#把和本地关联的配置(Flutter 根目录等)拷贝到对应的文件里面
# echo "正在删除本地Flutter配置相关文件"
# rm -rf ./ios/Flutter

# echo "正在拷贝本地关联的配置(Flutter 根目录等)"
# cp -R ./.ios/Flutter ./ios
echo "cd ./ios, rm Podfile.lock ,pod install"
cd ./ios
rm Podfile.lock
pod install

echo "done!"