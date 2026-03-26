# 森罗物语：群峦酒馆

本项目人工代码部分只有以下内容：
mixin，葡萄，葡萄藤，葡萄生长，葡萄成果物的各种基类和注册

AI制作了：
调用我封装的方法进行细节的实现和操作
AI注释维护管理工具

大量ai代码声明


AI注释提示：
我使用的ai注释工具只会自动维护我写过并且存在的注释，不会去新增注释

代码声明，代码中存在多处我离奇的妙妙小实现，这是无奈之举，有人可能会问
啊啊，酒馆啥都给你封装好了你怎么不用啊？
酒馆虽然有非常大量的拓展冗余，但是其毕竟只有一种葡萄，涵盖不全，有一些方法是硬编码的，所以我的实现方式是之间拉过来自己又实现一个一模一样的
来绕过一些毕竟麻烦的操作

本质上并没有做什么“兼容”，而是把隔壁写好的方法拉过来，然后自己写逻辑，又去注册新的方法

因为这个mod并没有长线随着酒馆升级而升级的打算

本mod是为群峦：野火整合包开发的兼容性mod，因此方向大多都是为了实现野火的需求，只需要保证野火能跑，一些兼容拓展性方面可能比较糟糕，还望理解
但是你依旧可以把你遇到的问题发到issue当中让我来解决，我会尽快回复你

模组处于开发阶段，以下是进度列表

## 许可证

本项目采用 [MIT License](LICENSE)。

**请注意**：本项目的部分代码基于 [森罗物语：酒馆](https://github.com/KaleidoscopeMods/KaleidoscopeTavern?tab=BSD-3-Clause-2-ov-file) 的工作，该部分代码遵循 [BSD 3-Clause License](licenses/BSD-3-Clause.txt)。

## 致谢 / 鸣谢

本项目在开发过程中使用了以下开源项目：

- **[森罗物语：酒馆](https://github.com/KaleidoscopeMods/KaleidoscopeTavern?tab=BSD-3-Clause-2-ov-file)**  – Copyright (c) 2025, Kaleidoscope Official Production Team. 根据 BSD 3-Clause License 授权。我们基于此代码进行了二次开发和功能封装。

## 许可证文件结构

- `LICENSE` – 本项目（新增代码）的 MIT 许可证
- `licenses/`
  - `Third-party-License` – 原始代码的 BSD 3-Clause 许可证副本
  - `ORIGIN-ASSETS` – 原项目的美术资源


## ⚠️ 重要许可证声明

本项目是一个**衍生作品**，主要基于 [森罗物语：酒馆](https://github.com/KaleidoscopeMods/KaleidoscopeTavern?tab=BSD-3-Clause-2-ov-file) 的工作构建。

由于我们使用了酒馆的美术资源（**模型素材**），并对其进行了修改/再创作，本项目的**整体使用和分发**需遵循 **Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)** 许可证。

### 详情如下：
1. **整体项目**：作为包含改编素材的衍生作品，遵循 [CC BY-NC-SA 4.0](licenses/CC-BY-NC-SA-4.0.txt)。
   - **非商业性**：不得用于商业目的。
   - **相同方式共享**：如果您分发修改后的版本，必须采用相同的 CC BY-NC-SA 4.0 许可证。

2. **子组件例外**：
   - **原始代码组件**：项目中源自原项目的代码部分，您可以**单独将其作为软件组件**使用，并遵循其原有的 [BSD 3-Clause License](licenses/BSD-3-Clause.txt)。
   - **我们新增的代码**：您可以**单独使用**我们编写的代码部分，并遵循附加的 [MIT License](LICENSE)。

3. **换言之**：
   - 如果您想**整体使用本项目**（含模型和代码） → **必须遵守 CC BY-NC-SA 4.0（非商业）**
   - 如果您只想**提取并使用其中的代码库** → 可以遵循更宽松的 BSD/MIT 条款
   - **不得将本项目用于任何商业用途**

详见完整的许可证文件。