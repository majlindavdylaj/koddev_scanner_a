import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:koddev_scanner_a/koddev_scanner_a.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  Uint8List? image1;
  Uint8List? image2;

  @override
  void initState() {
    super.initState();
    KoddevScanner_a.init();
  }


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              if(image1 != null)
                Image.memory(image1!),
              if(image2 != null)
                Image.memory(image2!),
            ],
          ),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () async {
            List<dynamic>? picture = await KoddevScanner_a.startScan();
            setState(() {
              image1 = Uint8List.fromList(picture[0]);
              image2 = Uint8List.fromList(picture[1]);
            });
          },
        ),
      ),
    );
  }
}
