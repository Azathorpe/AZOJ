#include <iostream>
#include <vector>

using namespace std;

int main(){
  int n;
  cin >> n;
  vector<int> arr;
  for(int i = 0;i < n;i++){
    int x;
    cin >> x;
    arr.push_back(x);
  }

  cout << arr[n / 2] << endl;

  return 0;
}
