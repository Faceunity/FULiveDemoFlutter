import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/business/homepage/homepage.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        backgroundColor: const Color(0xFF090017),
        appBar: AppBar(
          title: const Text("FULiveDemo 特效版", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 17),),
          backgroundColor: const Color(0xFF030010),
          bottom: PreferredSize(
            preferredSize: const Size(double.infinity, 1),
            child: SizedBox(
              child: Container(height: 1.0, color: const Color(0xFF302D33)),
            ),
          ),
          elevation: 0,
        ),
        body: const HomepageView(),
      ),
    );
  }
}


