import 'package:flutter/material.dart';
import 'package:fulivedemo_flutter/classes/beauty/filter/filter_view_model.dart';
import 'package:fulivedemo_flutter/common/slider_view.dart';
import 'package:fulivedemo_flutter/util/common_util.dart';
import 'package:fulivedemo_flutter/util/screen_util.dart';
import 'package:provider/provider.dart';

class FilterView extends StatefulWidget {
  final FilterViewModel viewModel;

  const FilterView({super.key, required this.viewModel});

  @override
  State<StatefulWidget> createState() {
    return FilterViewState();
  }
}

class FilterViewState extends State<FilterView> {
  
  @override
  Widget build(Object context) { 
    return ChangeNotifierProvider(
      create: (context) {
        return widget.viewModel;
      },
      child: Consumer<FilterViewModel>(builder: (context, value, child) {
        return Container(
          color: const Color.fromARGB(200, 0, 0, 0),
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.max,
              verticalDirection: VerticalDirection.up,
              children: [
                _providerList(),
                widget.viewModel.selectedIndex > 0 ? 
                SizedBox(
                  height: 50,
                  width: ScreenUtil.getScreenW(context) - 112,
                  child: SliderView(
                    value: widget.viewModel.filters.isNotEmpty ? widget.viewModel.filters[widget.viewModel.selectedIndex].filterLevel : 0,
                    onChanged: (value) {
                      widget.viewModel.setFilterLevel(value);
                    },
                  ),
                ) : const SizedBox(
                  height: 50,
                )
              ],
            )
          )
        );
      },),
    );
  }

  Widget _providerList() {
    return SizedBox(
      height: 90,
      child: ListView.builder(
        padding: const EdgeInsets.fromLTRB(10, 0, 0, 0),
        itemBuilder: (context, index) {
          return _itemCell(index);
        },
        itemCount: widget.viewModel.filters.length,
        scrollDirection: Axis.horizontal
      )
    );
  }

  Widget _itemCell(int index) {
    String filterName = widget.viewModel.filters[index].filterName;
    return TextButton(
      style: const ButtonStyle(
        backgroundColor: MaterialStatePropertyAll(Colors.transparent),
        overlayColor: MaterialStatePropertyAll(Colors.transparent)
      ),
      onPressed: () {
        setState(() {
          widget.viewModel.setSelectedIndex(index);
        });
      }, 
      
      child: Column(
        children: [
          Container(
            height: 54,
            width: 54,
            decoration: BoxDecoration(
              image: DecorationImage(image: CommonUtil.assetImageNamed("beauty/filter/$filterName")),
              border: Border.all(
                color: widget.viewModel.selectedIndex == index ? const Color(0xFF5EC7FE) : Colors.transparent,
                width: 2
              ),
              borderRadius: BorderRadius.circular(3.0)
            ),
          ),
          SizedBox(
            height: 16,
            width: 54,
            child: Center(
              child: Text(filterName, style: const TextStyle(fontSize: 10, color: Colors.white), textAlign: TextAlign.center,),
            )
          )
        ],
      )
    );
  }

}