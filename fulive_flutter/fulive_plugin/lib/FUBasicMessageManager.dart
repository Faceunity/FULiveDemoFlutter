import 'dart:async';

import 'package:flutter/services.dart';

class FUBasicMessageManager {
  static const BasicMessageChannel _channel =
      const BasicMessageChannel('fulive_plugin_routes', JSONMessageCodec());

  static Future<int?> fuRoutes(int itemType) async {
    final int? ret = await _channel.send({"itemType": itemType});
    // await _channel.invokeMethod("FURoutes", {'itemType': itemType});
    return ret;
  }
}
