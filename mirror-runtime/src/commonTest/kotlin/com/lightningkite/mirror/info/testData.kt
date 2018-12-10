package com.lightningkite.mirror.info

interface TestInterface

open class TestParent : TestInterface

interface TestAnotherInterface

class TestChild() : TestParent(), TestAnotherInterface