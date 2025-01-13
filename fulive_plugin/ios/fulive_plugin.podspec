#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint fulive_plugin.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'fulive_plugin'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter plugin project.'
  s.description      = <<-DESC
A new Flutter plugin project.
                       DESC
  s.homepage         = 'http://www.faceunity.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'faceunity' => 'linpingxiang@faceunity.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'FURenderKit_flutter', '8.13.0'
  s.dependency 'YYModel', '1.0.4'
  s.static_framework = true
  s.resource_bundles = {
    'fulive_plugin' => ['Assets/*.json','**/*.{png,bundle,json}']
  }
  s.platform = :ios, '12.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
end
