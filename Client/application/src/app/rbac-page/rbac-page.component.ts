import { Component, OnInit } from '@angular/core';
import { GenericService } from '../services/generic.service';
import { ToastrService } from 'ngx-toastr';
import { Role } from '../model/role';
import { Permission } from '../model/permission';

@Component({
  selector: 'app-rbac-page',
  templateUrl: './rbac-page.component.html',
  styleUrls: ['./rbac-page.component.css']
})
export class RbacPageComponent implements OnInit {

  relativeUrlRoles: string = '/rbac/get_roles';
  relativeUrlPermissions: string = '/rbac/get_permissions';
  relativeUrlAddPermissionToRole: string = 'rbac/add-permission-to-role';
  relativeUrlRemove: string = 'rbac/remove-permission-to-role';
  roles: Role[];
  permissions: Permission[];
  newPermissionId: number;

  constructor(private genericService: GenericService, private toastr: ToastrService) { }

  ngOnInit() {
    this.getRoles();
    this.getPermissions();
  }


  getRoles() {
    this.genericService.getAll<Role>(this.relativeUrlRoles) .subscribe(
      (roles: Role[]) => {
        this.roles = roles;
        if (this.roles) {
          if (this.roles.length > 0) {
            this.toastr.success('Roles are successfully loaded!');
          }
          else {
            this.toastr.warning('There are no roles at the moment!');
          }
        }
        else {
          this.toastr.error('Problem with loading roles!');
        }
      },
      err => this.toastr.error('Error: ' + JSON.stringify(err))
    );
  }

  getPermissions() {
    this.genericService.getAll<Permission>(this.relativeUrlPermissions) .subscribe(
      (permissions: Permission[]) => {
        this.permissions = permissions;
        if (this.permissions) {
          if (this.permissions.length > 0) {
            this.toastr.success('Permissions are successfully loaded!');
            this.newPermissionId = this.permissions[0].id;
          }
          else {
            this.toastr.warning('There are no permissions at the moment!');
          }
        }
        else {
          this.toastr.error('Problem with loading permissions!');
        }
      },
      err => this.toastr.error('Error: ' + JSON.stringify(err))
    );
  }

  addPermissionToRole(roleId: number) {
    this.genericService.post(this.relativeUrlRoles, {'roleId': roleId, 'permissionId': this.newPermissionId}).subscribe(
      () => {
            this.getRoles();
            this.toastr.success('Permission successfully add to role!');
      },
      err => this.toastr.error('Error: ' + JSON.stringify(err))
    );
  }

  removePermissionFromRole(roleId: number, permissionId: number) {
    this.genericService.deletePermissionFromRole(this.relativeUrlRemove, roleId, permissionId) .subscribe(
      () => {
        this.getRoles();
        this.toastr.success('Permission successfully remove from role!');
      },
      err => this.toastr.error('Error: ' + JSON.stringify(err))
    );
  }

}
