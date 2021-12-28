extension ArrayExtension on List {
  //越界判断
  bool inRange(index) {
    if (index < this.length && index >= 0) {
      return true;
    }
    return false;
  }
}
