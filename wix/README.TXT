Az MSI telepítõ legyártása Windows rendszer alatt az alábbi parancsok végrehajtásával lehetséges:
candle -ext WixUtilExtension Setup.wxs
light -ext WixUIExtension -ext WixUtilExtension -loc hu.wxs -cultures:hu-hu Setup.wixobj
