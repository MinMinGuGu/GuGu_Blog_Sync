<div align="center">
    <p>
        <img src="https://img.shields.io/badge/Java-11-brightgreen"/>
        <img src="https://img.shields.io/badge/GuGu__Blog__Sync-v0.0.1-green"/>
        <a href="https://app.fossa.com/projects/git%2Bgithub.com%2FMinMinGuGu%2FGuGu_Blog_Sync?ref=badge_shield">
            <img src="https://app.fossa.com/api/projects/git%2Bgithub.com%2FMinMinGuGu%2FGuGu_Blog_Sync.svg?type=shield" />
        </a>
    </p>
</div>
<p align="center">基于Github仓库的Webhook实现的个人博客文章同步框架。</p>
<p align="center">它会处理Markdown文件的变更操作，支持自定义处理初始化、新增、删除、修改实现。</p>
<p align="center">现有的博客框架支持：<a target="_black" href="https://github.com/MinMinGuGu/GuGu_Blog_Sync_Halo">Halo - GuGu_Blog_Sync_Halo</a></p>

## 特性

- 文章使用Github仓库进行版本控制
- 支持自定义处理Markdown文件更改时的动作
- 提供启动器模块，快速引入，便捷对接其他博客平台
- 规范Markdown文章元数据格式定义，并支持多种数据交换格式
- 支持配置代理来操作Git

## 使用

### 引入

目前还不稳定，暂时不会考虑上传到Maven的中心仓库，请使用Maven进行install。

#### 当作父依赖

```xml

<parent>
    <groupId>com.gugumin</groupId>
    <artifactId>gugu_blog_sync</artifactId>
    <version>0.0.1</version>
</parent>
```

#### 当依赖引入

```xml

<dependency>
    <groupId>com.gugumin</groupId>
    <artifactId>gugu_blog_sync_starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 接入模板

[GuGu_Blog_Sync_Example](https://github.com/MinMinGuGu/GuGu_Blog_Sync_Example)

## 文档

请转到 [Wiki](https://github.com/MinMinGuGu/GuGu_Blog_Sync/wiki)

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FMinMinGuGu%2FGuGu_Blog_Sync.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FMinMinGuGu%2FGuGu_Blog_Sync?ref=badge_large)
