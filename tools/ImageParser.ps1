Function Resize-Image
{
    param
    (
    [Switch]$Percent,
    [float]$Percentage,
    [Switch]$Pixels,
    [int]$Width,
    [int]$Height
    )
 
    begin
    {
        if( $Percent -and $Pixels)
        {
            Write-Error "按照百分比(Percent)或者分辨率(Pixels)缩放，只能任选其一奥！"
            break
        }
        elseif($Percent)
        {
            if($Percentage -le 0)
            {
              Write-Error "参数Percentage的值必须大于0！"
              break
            }
        }
        elseif($Pixels)
        {
            if( ($Width -lt 1) -or ($Height -lt 1))
            {
              Write-Error "参数Width和Height的值必须大于等于1！"
              break
            }
        }
        else
        {
            Write-Error "请选择按照百分比(-Percent)或者分辨率(-Pixels)缩放！"
            break
        }
        Add-Type -AssemblyName 'System.Windows.Forms'
        $count=0
 
    }
    process
    {
        $items = Get-ChildItem -Filter "*.jpg" | Select FullName
        foreach ($item in $items)
        {
            $img=[System.Drawing.Image]::FromFile($item.FullName)
 
            # 按百分比重新计算图片大小
            if( ($Percentage -gt 0) -and ($Percentage -ne 1.0) )
            {
                $Width = $img.Width * $Percentage
                $Height = $img.Height * $Percentage
            }
 
            # 缩放图片
            $size = New-Object System.Drawing.Size($Width,$Height)
            $bitmap =  New-Object System.Drawing.Bitmap($img,$size)
 
            # 保存图片
            $img.Dispose()
            $bitmap.Save($item.FullName)
            $bitmap.Dispose()
 
            $count++
        }
    }
    end
    {
        "完毕，共处理 $count 了个文件"
    }
}