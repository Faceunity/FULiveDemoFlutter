import 'package:flutter/material.dart';

class CustomPageRoute<T> extends MaterialPageRoute<T> {

  CustomPageRoute({required super.builder});

  @override
  Widget buildTransitions(BuildContext context, Animation<double> animation, Animation<double> secondaryAnimation, Widget child) {
    return SlideTransition(
      position: Tween<Offset>(
        begin: const Offset(1.0, 0.0),
        end: Offset.zero
      ).chain(CurveTween(curve: Curves.ease)).animate(animation),
      child: child,
    );
  }

  @override
  Duration get transitionDuration => const Duration(milliseconds: 200);
}