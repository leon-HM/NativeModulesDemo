import React, {Component} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  TouchableHighlight,
  useColorScheme,
  View,
  Image,
  Button,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';

import ImageCrop from './ImageCrop';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      x: '1',
      y: '1',
      imgUri: {},
      screenWidth: 0,
      screenHeight: 0,
    };
  }

  render() {
    return (
      <SafeAreaView>
        <ScrollView contentInsetAdjustmentBehavior="automatic">
          <View style={{flexDirection: 'row', alignItems: 'center'}}>
            <Text style={styles.label}>宽：</Text>
            <TextInput
              defaultValue={this.state.x}
              onChangeText={text => this.setState({x: text})}
              style={styles.input}
            />
            <Text style={styles.label}>比 高:</Text>
            <TextInput
              style={styles.input}
              defaultValue={this.state.y}
              onChangeText={text => this.setState({y: text})}
            />
            <TouchableHighlight onPress={() => this.onSelectCrop()}>
              <Text style={{width: 100, fontSize: 20,backgroundColor:'blue',color:'white'}}>裁剪</Text>
            </TouchableHighlight>
          </View>
          <Image
            style={{width: 300, height: 300, resizeMode: 'contain'}}
            source={this.state.imgUri}
          />
          <Button title="测试发送事件" onPress={() => this.onGetScreenSize()} />
          <Text>
            屏幕宽:{this.state.screenWidth} 高:{this.state.screenHeight}
          </Text>
        </ScrollView>
      </SafeAreaView>
    );
  }

  onGetScreenSize() {
    console.log('getScreenSize()', ImageCrop);
    ImageCrop.getScreenSize();
  }

  async onSelectCrop() {
    let x = this.state.x;
    let y = this.state.y;
    try {
      console.log('onSelectCrop', ImageCrop);
      var result = await ImageCrop.selectWithCrop(parseInt(x), parseInt(y));
      console.log('onSelectCrop result:', result);
      this.setState({
        imgUri: {uri: 'file://' + result},
      });
    } catch (e) {
      console.log(e);
    }
  }

  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(NativeModules.ImageCrop);
    this.eventListener = eventEmitter.addListener('eventScreenSize', event => {
      console.log(event);
      this.setState({
        screenWidth: event.screenWidth,
        screenHeight: event.screenHeight,
      });
    });
  }

  componentWillUnmount() {
    this.eventListener.remove();
  }
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  label: {
    fontSize: 20,
  },
  input: {
    fontSize: 20,
    width: 30,
  },
});

export default App;
