//
//  ContentView.swift
//  iosApp
//
//  Created by Chechin Vadim on 17.05.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import SwiftUI
import SharedCode

struct ContentView: View {
    var body: some View {
        Text("Hello, World! " + CommonKt.createApplicationScreenMessage())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
