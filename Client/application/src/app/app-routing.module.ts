import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SendMessageComponent } from './send-message/send-message.component';



const routes: Routes = [
  { path: 'send-message', component: SendMessageComponent},
 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
