# Ovu skriptutreba pokretati kao Administrator

$group = "SiemAgentGroup"
$groupUsers = "Users"
# $user = "siem_center"
$user = "siem_agent"

function Strings-Contains {
 foreach($name in $args[0]) {
	if($name -Match $args[1]) {
		return $true
	}
 }
 
 return $false;
}

$GroupExists = Get-LocalGroup -Name $group
if ($GroupExists -eq $NULL) {
	New-LocalGroup -Name $group
}

# Samo proveravamo da li vec postoji ovaj user 
$userExist =(Get-LocalUser).Name -Contains $user
if(-Not $userExist) {
	$SecureStringPassword = ConvertTo-SecureString $user -AsPlainText -Force
    New-LocalUser -Name $user -Password $SecureStringPassword
	Write-Host "Kreiran novi lokalni User"
}

$userInGroupUsers = Strings-Contains (Get-LocalGroupMember $groupUsers).Name $user
if (-Not $userInGroupUsers) {
	Add-LocalGroupMember -Group $groupUsers -Member $user  # najpre ga dodajemo u Users grupu da bismo mogli da se ulogujemo na ovaj nalog
} 

$userInGroup = Strings-Contains (Get-LocalGroupMember $group).Name $user
if (-Not $userInGroup) {
	Add-LocalGroupMember -Group $group -Member $user
}

$path = ".\bez_veze"
$acl = Get-Acl $path

# $AccessRule = New-Object System.Security.AccessControl.FileSystemAccessRule($user,"Read","Allow")
$AccessRule1 = New-Object System.Security.AccessControl.FileSystemAccessRule($group, 'Read','ContainerInherit,ObjectInherit', 'None', 'Allow')
$acl.SetAccessRule($AccessRule1)


$AccessRule2 = New-Object System.Security.AccessControl.FileSystemAccessRule($group, 'Write','ContainerInherit,ObjectInherit', 'None', 'Deny')
$acl.SetAccessRule($AccessRule2)

#$acl.RemoveAccessRule($AccessRule3)

$acl | Set-Acl $path