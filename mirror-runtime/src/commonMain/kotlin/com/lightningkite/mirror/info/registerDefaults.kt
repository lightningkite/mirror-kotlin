package com.lightningkite.mirror.info



fun registerDefaults() {
    MirrorClassMirror.register(
            UnitMirror,
            BooleanMirror,
            ByteMirror,
            ShortMirror,
            IntMirror,
            LongMirror,
            FloatMirror,
            DoubleMirror,
            CharMirror,
            StringMirror,
            MirrorClassMirror,
            MirrorClassFieldMirror,
            AnyMirror,
            ListMirror.minimal,
            MapMirror.minimal,
            SetMirror.minimal
    )
}
