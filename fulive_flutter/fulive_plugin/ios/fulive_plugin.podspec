#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint fulive_plugin.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'fulive_plugin'
  s.version          = '0.0.1'
  s.summary          = 'A new flutter plugin project.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = ['Classes/**/*']
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'FURenderKit', '7.4.1'
  s.dependency 'SVProgressHUD'
  s.dependency 'MJExtension', '3.0.15.1'
  s.static_framework = true
  s.resource_bundles = {
    'fulive_plugin' => ['Assets/*.json','**/*.{png,bundle,json}']
  }
  
  s.platform = :ios, '8.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
end
