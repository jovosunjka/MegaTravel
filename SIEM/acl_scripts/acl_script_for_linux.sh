# Ova skripta se mora pokrenuti kao root
# Primer pokretanja: sudo ./acl_script_for_linux.sh
# Nakon ovog treba uneti password
# Nakon unosa ispravnog password-a izvrsice se skripta

group=SiemAgentGroup
user=siem_agent
path=./bez_veze

if [ ! $(getent group $group) ]
then
    groupadd $group
    echo Kreirana nova Grupa
else
    echo Grupa $group vec postoji
fi

if [ ! $(getent passwd $user) ]
then
    password=$(perl -e 'print crypt($ARGV[0], "password")' $user)
	useradd -m -p $password $user
    echo Kreiran novi User
else
    echo User $user vec postoji
fi

if groups $user | grep -q "\b$group\b"
then
    echo User $user je jos pre dodat u grupu $group
else
    usermod -a -G $group $user
    # usermod -g $group $user # postavljanje grupe da bude Primary Group za ovog usera
    echo User $user je dodat u grupu $group
fi
 
setfacl -m "g:$group:r" $path
echo ACL je podesen za $path
