

import 'package:flutter/services.dart';

class KoddevScanner_a {
  static const MethodChannel _channel = MethodChannel('koddev_document_scanner');

  static init() {
    _channel.invokeMethod('initScan');
  }

  static Future<List<dynamic>> startScan() async {
    final List<dynamic>? picture = await _channel.invokeMethod('startScan');
    print("------$picture");
    return picture!;
  }
}
